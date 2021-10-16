package booking.controller;

import booking.model.*;
import booking.repository.*;
import booking.security.AllowedForAdmin;
import booking.security.AllowedForHotelManager;
import booking.security.AllowedForManageHotel;
import booking.util.HotelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value="/hotels")
public class HotelController {

	@Autowired
	HotelRepository hotels;

	@Autowired
    CategoryRepository categories;

	@Autowired
    RoomTypeRepository roomTypes;

	@Autowired
	RoomRepository rooms;

	@Autowired
	UserRepository users;
	
	@Autowired
	ImageRepository images;
	
	@Autowired
	CommentRepository comments;
	
	@Autowired
    BookingRepository bookings;

	@RequestMapping(method= RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("hotels", hotels.findAll());
		return "hotels/index";
	}

	@RequestMapping(method= RequestMethod.GET, produces={"text/plain","application/json"})
	public @ResponseBody
    Iterable<Hotel> indexJSON(Model model) {
		return hotels.findAll();
	}

	@RequestMapping(value="/new", method= RequestMethod.GET)
	@AllowedForHotelManager
	public String newHotel(Model model) {
		model.addAttribute("hotel", new Hotel());
		model.addAttribute("categories", categories.findAll());
		return "hotels/create";
	}

	@RequestMapping(method= RequestMethod.POST)
	@AllowedForHotelManager
	public String saveIt(@ModelAttribute Hotel hotel, Model model) {
		hotel.setManager(getCurrentUser());
		hotels.save(hotel);
		model.addAttribute("hotel", hotel);
		return "redirect:/hotels";
	}

	@RequestMapping(value="{id}", method= RequestMethod.GET)
	public String show(@PathVariable("id") long id, Model model) {
		Hotel hotel = hotels.findOne(id);
		if( hotel == null )
			throw new HotelNotFoundException();
		Iterable<Comment> hotel_comments = comments.getComments(id);
    	
		model.addAttribute("booking", new Booking());
    	model.addAttribute("comments", hotel_comments);
		model.addAttribute("hotel", hotel );
		model.addAttribute("reply", new Comment());
		model.addAttribute("users", users.findAll());
		model.addAttribute("roomTypes", roomTypes.findAll());
		
		Map<Long, Room> rmap = hotel.getRooms();
		Map<RoomType, Room> rttemp = new HashMap<RoomType, Room>();
		
		for(Room r : rmap.values())
		{
			rttemp.put(r.getType(), r);
		}
		
		model.addAttribute("hotelRoomTypes", rttemp);		
		return "hotels/show";
	}

	@RequestMapping(value="{id}", method= RequestMethod.GET, produces={"text/plain","application/json"})
	public @ResponseBody
    Hotel showJSON(@PathVariable("id") long id, Model model) {
		Hotel hotel = hotels.findOne(id);
		if( hotel == null )
			throw new HotelNotFoundException();
		return hotel;
	}

	@RequestMapping(value="{id}/edit", method= RequestMethod.GET)
	@AllowedForManageHotel
	public String edit(@PathVariable("id") long id, Model model) {
		Hotel hotel = hotels.findOne(id);
		model.addAttribute("hotel", hotel);    	
		model.addAttribute("categories", categories.findAll());
		model.addAttribute("users", users.findAll());
		return "hotels/edit";
	}

	@RequestMapping(value="{id}", method= RequestMethod.POST)
	@AllowedForManageHotel
	public String editSave(@PathVariable("id") long id, @ModelAttribute("hotel") Hotel hotel) {
		hotel.setStatus(false);
		hotels.save(hotel);
		return "redirect:/hotels/{id}";
	}

	@RequestMapping(value="{id}/remove", method= RequestMethod.GET)
	@AllowedForManageHotel
	public String remove(@PathVariable("id") long id, Model model)
	{		
		for(Room r : hotels.findOne(id).getRooms().values())
		{
			for(Booking b : r.getBookings())
			{
				bookings.delete(b);
			}
		}
		
		hotels.delete(hotels.findOne(id));
		return "redirect:/hotels";
	} 
	
	@RequestMapping(value="{id}/approve", method= RequestMethod.GET)
	@AllowedForAdmin
	public String approve(@PathVariable("id") long id, Model model) {
		Hotel h = hotels.findOne(id);
		h.setStatus(true);
		hotels.save(h);
		return "redirect:/admin";
	}  

	@RequestMapping(value="{id}/upload", method= RequestMethod.POST)
	@AllowedForManageHotel
	public String uploadImage(@PathVariable("id") long id, Model model, @RequestParam("files") MultipartFile files[] ){
	
		if(files.length > 0)
		{
			for(int i=0; i < files.length; i++){
				
				MultipartFile file = files[i];
				try {
					byte[] bytes = file.getBytes();
					String path = "src/main/resources/public/static/" + file.getOriginalFilename();
					BufferedOutputStream stream =
							new BufferedOutputStream(new FileOutputStream(new File(path)));
					stream.write(bytes);
					stream.close();
					
					Image image = new Image();
					image.setHotel(hotels.findOne(id));
					image.setInsertion_date(new Date());
					image.setPath(file.getOriginalFilename());
					images.save(image);

				} catch (Exception e) {
				}
			}
			return "redirect:/hotels/{id}/upload";
		}
		return "";
	}
	
	@RequestMapping(value="{id}/upload", method= RequestMethod.GET)
	@AllowedForManageHotel
	public String uploadForm(@PathVariable("id") long id, Model model) {
		Hotel hotel = hotels.findOne(id);
		model.addAttribute("hotel", hotel);    			
		return "hotels/upload";
	}
	
	@RequestMapping(value="{id}/remove_image/{id_image}", method= RequestMethod.GET)
	@AllowedForManageHotel
	public String deleteImage(@PathVariable("id") long id, @PathVariable("id_image") long id_image, Model model) {
		Image image = images.findOne(id_image);
		images.delete(image);
		return "redirect:/hotels/{id}/upload";
	}
	
	@RequestMapping(value="/search", method= RequestMethod.GET)
	public String searchHotel(Model model, @RequestParam("searchString") String searchString)
	{
		Iterator<Hotel> ithotels = hotels.findAll().iterator();
    	List<Hotel> hotelsList = new ArrayList<Hotel>();
    	    	
    	while(ithotels.hasNext())
    	{
    		Hotel h = ithotels.next();
    		if(h.getName().toLowerCase().contains(searchString.toLowerCase()))
    			hotelsList.add(h);
    	}				
    	
    	model.addAttribute("hotels", hotelsList);
    	return "hotels/index";
	}
	
	@RequestMapping(value="{id}/map", method= RequestMethod.POST)
	@AllowedForHotelManager
	public String hotelMap(@PathVariable("id") long id, Model model, @ModelAttribute Booking booking)
	{		
		model.addAttribute("begin_date", booking.getBegin_date());
		model.addAttribute("end_date", booking.getEnd_date());
		model.addAttribute("hotel", hotels.findOne(id));		
		model.addAttribute("occupancy", getOccupancy(hotels.findOne(id), booking.getBegin_date(), booking.getEnd_date()));
    	return "hotels/map";
	}

	public Map<Room, Map<Date, Boolean>> getOccupancy(Hotel hotel, Date begining, Date end)
	{		
		List<Date> dates = new LinkedList<Date>();
		Calendar calendar = new GregorianCalendar();
		
		calendar.setTime(begining);

		while (calendar.getTime().getTime() <= end.getTime()){
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);  
		}
		
		Map<Room, Map<Date, Boolean>> result = new TreeMap<Room, Map<Date, Boolean>>();
		
		for (Room r : hotel.getRooms().values())
		{
			Map<Date, Long> days_reserved = r.getDays_reserved();
			Map<Date, Boolean> roomOcc = new TreeMap<Date, Boolean>();
			
			for (Date d : dates)
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				String date = fmt.format(d);
				
				for(Date day_reserved : days_reserved.keySet())
				{
					String day = fmt.format(day_reserved);
					
					if(date.equals(day))
					{
						roomOcc.put(d, true);
					}
					else
					{
						if(!(roomOcc.containsKey(d) && roomOcc.get(d)))
							roomOcc.put(d, false);
					}
				}
				
				if(days_reserved.isEmpty())
					roomOcc.put(d, false);
			}

			result.put(r, roomOcc);
		}
		
		return result;
	}
	
	 private User getCurrentUser(){
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CustomUserDetail myUser= (CustomUserDetail) auth.getPrincipal(); 
			return myUser.getUser();
	    }
}
