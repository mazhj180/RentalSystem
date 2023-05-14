package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import Pojo.Hetong;
import Pojo.User;
import Pojo.Userlist;
import Pojo.Zulist;
import service.UserlistService;
import service.ZulistService;

@Controller
@RequestMapping("/zulist")
public class ZulistController {
	@Autowired
	private ZulistService zulistService;
	@Autowired
	private UserlistService userlistService;

	/**
	 * 管理员在看房申请页面点击同意租赁 跳转到添加合同页面
	 * @param model
	 * @param house_id
	 * @return
	 */
	@RequestMapping("/toaddhetong")
	public String toaddhetong(Model model,String house_id){
		Hetong hetong=new Hetong();
		hetong.setHouse_id(house_id);
		model.addAttribute("hetong", hetong);
		model.addAttribute("mainPage", "addhetong.jsp");
		
		return "admin/main1";
	}
	//管理员查看所有在租列表

	/**
	 * 管理员查看所有在租房源（分页展示）
	 * @param model
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findzulist")
	public String findzulist(Model model,@RequestParam(required=false,defaultValue="1") Integer page,
            @RequestParam(required=false,defaultValue="6") Integer pageSize) throws Exception{
		//分页查询
		PageHelper.startPage(page, pageSize);
		List<Zulist> zulist=zulistService.findzuuserlist();
		PageInfo<Zulist> p=new PageInfo<Zulist>(zulist);
		//返回信息
		model.addAttribute("p", p);
		model.addAttribute("zulist", zulist);
		model.addAttribute("mainPage", "zulist.jsp");
		return "admin/main1";
	}

	/**
	 * 租客查看我的在租列表 （分页展示）
	 * @param model
	 * @param httpSession
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myzulist")
	public String myzulist(Model model,HttpSession httpSession,@RequestParam(required=false,defaultValue="1") Integer page,
            @RequestParam(required=false,defaultValue="6") Integer pageSize) throws Exception{
		//从session中获取user信息
		User user1= (User) httpSession.getAttribute("user");

		Userlist userlist=userlistService.findhasuserlist(user1.getId());
		PageHelper.startPage(page, pageSize);
		List<Userlist> list=userlistService.getUserzuList(userlist.getId());
		PageInfo<Userlist> p=new PageInfo<Userlist>(list);
		model.addAttribute("userlistzu", list);
		model.addAttribute("p", p);
		model.addAttribute("mainPage", "myzulist.jsp");
		return "zuke/main";
	}
	
}
