package controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import Pojo.User;
import service.UserService;

/**
 * 登录注册模块
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 转发到login.jsp页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/login")
	public String userList() throws Exception{
		return "login";
	}

	/**
	 * 转发到register.jsp页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/register")
	public String registerlist() throws Exception{
		return "register";
	}

	/**
	 * 用户登录验证
	 *
	 * @param user
	 * @param model
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logincheck")
	public String login(User user,Model model,HttpSession httpSession) throws Exception{
		//调用方法查询数据中是否存在该用户
		User user1=userService.login(user);
		if(user1!=null){ //不等于空 代表存在可以登录
			//将该用户对象放入session域中
			httpSession.setAttribute("user", user1);
			//判断用户的角色 是租客 还是 管理员
			if(user1.getType().equals("zuke")){ //角色位租客
				//重定向到租客的页面
				return "redirect:houselist.action";
			}
			else{ //角色是管理员
				//重定向到租客的页面
				return "redirect:ahouselist.action";
			}
		}else{
			String error="error";
			model.addAttribute("error", error);
		return "login";
		}
	}

	/**
	 * 用户注册
	 * 注：用户的密码在前端经过了md5加密
	 * 	将加密后的密码插入了数据库
	 * 	例 ：我设置的密码是123
	 * 		加密后得到 202cb962ac59075b964b07152d234b70
	 *
	 * @param user
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/registercheck")
	public String register(User user,Model model) throws Exception{
		//判断数据库中是否存在在该用户
		User user1=userService.register(user);
		if(user1==null){ //不存在
			//将注册的用户信息插入到数据库user表中
			boolean registerStatu=userService.insertUser(user);
			if(registerStatu){
				return "login";
			}
			String error="error";
			model.addAttribute("error", error);
				return "register";
		}else{
			String error="error";
			model.addAttribute("error", error);
		return "register";
		}
	}

	/**
	 * 跳转到租客主页
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toindex")
	public String toindex(Model model) throws Exception{
		//System.out.println("我是跳转到zuke/main");
		return "zuke/main";
		}

	/**
	 * 跳转到管理员主页
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/atoindex")
	public String atoindex(Model model) throws Exception{
		//System.out.println("我是跳转到admin/main1");
		return "admin/main1";
		}
	
	}

