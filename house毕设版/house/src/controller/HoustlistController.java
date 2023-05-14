package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import Pojo.Houselist;
import Pojo.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import service.HouselistService;

/**
 * 房源信息模块
 */
@Controller
public class HoustlistController {
	@Autowired  //根据类型自动注入houselistService 实例对象
	private HouselistService houselistService;

	/**
	 * 执行分页查询 展示房源列表（租客）
	 * @param model 模型对象用来保存数据
	 * @param page	当前前端展示的页号
	 * @param pageSize 每页的数据容量
	 * @return
	 */
	@RequestMapping("/houselist")
	public String houselist(Model model ,@RequestParam(required=false,defaultValue="1") Integer page,
            @RequestParam(required=false,defaultValue="6") Integer pageSize){
		//获取前端数据进行分页查询
		 PageHelper.startPage(page, pageSize);
		List<Houselist> houselist=houselistService.selectAll();
	PageInfo<Houselist> p=new PageInfo<Houselist>(houselist);
		//将查询到的数据放入request作用域中
		model.addAttribute("p", p);
		model.addAttribute("houselist",houselist);
		model.addAttribute("mainPage","houselist.jsp");
		return "zuke/main";
	}

	/**
	 * 查询房源 （租客）
	 * @param model
	 * @param page 当前前端展示的页号
	 * @param pageSize 每页的数据容量
	 * @param price 房源的价格 （根据价格查找）
	 * @param area 房源的区域 （根据区域查找）
	 * @param status 房源的状态 （根据状态查找）
	 * @param address 房源的地址 （根据地址查找）
	 * @return
	 */
	@RequestMapping("/selects")
	public String selects(Model model ,@RequestParam(required=false,defaultValue="1") Integer page, 
			@RequestParam(required=false,defaultValue="6") Integer pageSize,@RequestParam(value="price")String price,
			@RequestParam(value="area")String area,@RequestParam(value="status")String status,@RequestParam(value="address")String address){
		 PageHelper.startPage(page, pageSize);
		 //根据 price,area,status,address 分页查询房源 返回一个房源列表 即houselist
		List<Houselist> houselist=houselistService.selects(price,area,status,address);
	PageInfo<Houselist> p=new PageInfo<Houselist>(houselist);

	//将房源列表交给jsp页面 展示到前端
		model.addAttribute("p", p);
		model.addAttribute("houselist",houselist);
		model.addAttribute("mainPage","houselist.jsp");
		return "zuke/main";
	}

	/**
	 * 执行分页查询 展示房源列表（管理员）
	 * @param model 模型对象用来保存数据
	 * @param page 当前前端展示的页号
	 * @param pageSize 每页的数据容量
	 * @param request 用来获取请求的信息和作用域中的数据
	 * @return
	 */
	@RequestMapping("/ahouselist")
	public String ahouselist(Model model ,@RequestParam(required=false,defaultValue="1") Integer page,
            @RequestParam(required=false,defaultValue="6") Integer pageSize,HttpServletRequest request){
		//从session中获取当前登录的用户对象
		User username = (User) request.getSession().getAttribute("user");
		//分页查询操作
		 PageHelper.startPage(page, pageSize);
		List<Houselist> houselist=houselistService.selectLandladyAll(username.getUsername());
	PageInfo<Houselist> p=new PageInfo<Houselist>(houselist);

		//将查询到的数据放入request作用域中，方便在admin/main1.jsp页面中使用EL表达式取值
		model.addAttribute("p", p);
		model.addAttribute("houselist",houselist);
		model.addAttribute("mainPage","ahouselist.jsp");
		//转发到admin/main1.jsp页面
		return "admin/main1";
	}

	/**
	 * 添加房源（管理员）
	 * @param model
	 * @param houselist 映射数据库中的房源信息表
	 * @param request
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@RequestMapping("/addhouse")
	public String addhouse(Model model ,Houselist houselist,HttpServletRequest request) throws IllegalStateException, IOException{
		//System.out.println("houseid===="+request.getParameter("houseid"));

		//获取前端 需添加房源id
		String houseid=houselist.getHouseid();
		//判断数据库中是否存在 该房源
		Houselist houselist1=houselistService.findhouseid(houseid);
		if(houselist1!=null){ //houserlist1 不等于null 证明数据库中存在该房源 不能添加
			//将错误信息放到作用域中 jsp页面可直接获取并展示
			model.addAttribute("error","该房屋id已存在");
			model.addAttribute("houselist",houselist);
			model.addAttribute("mainPage","addhouse.jsp");
			return "admin/main1";
		}else{ // 反之 可以添加房源
			//将成功的消息放入域中
			model.addAttribute("error","添加成功");
			//System.out.println("houseid===="+request.getParameter("houseid"));

			//保存用户上传的图片

			//保存数据库的路径
			String sqlPath = null;
			//定义文件保存的本地路径
			String localPath= request.getServletContext().getRealPath("/imgs");
//			System.out.println(localPath);
			// 定义 文件名
			String filename=null;
			//判断是否上传文件
			if(!houselist.getFile().isEmpty()){ //不为空


			//生成uuid作为文件名称
			String uuid = UUID.randomUUID().toString().replaceAll("-","");
			//获得文件类型（可以判断如果不是图片，禁止上传）
			String contentType=houselist.getFile().getContentType();
			//获得文件后缀名
			String suffixName=contentType.substring(contentType.indexOf("/")+1);
			//得到 文件名
			filename=uuid+"."+suffixName;
//			System.out.println(filename);
			// 拷贝用户上传的文件到服务器本地
				File file = new File(localPath+"\\"+filename);
				if(!file.exists()){
					//先得到文件的上级目录，并创建上级目录，在创建文件
					file.getParentFile().mkdir();
					try {
						//创建文件
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			houselist.getFile().transferTo(file);
			}
			//把图片的相对路径保存至数据库
			sqlPath = "/imgs/"+filename;
			//user.setId(1);
			User username = (User) request.getSession().getAttribute("user");
			//设置房源的信息
			houselist.setHouseid(request.getParameter("houseid")); //房源的id
			houselist.setAddress(request.getParameter("address")); //房源地址
			houselist.setArea(Integer.parseInt(request.getParameter("area"))); //房源区域
			houselist.setPrice(Integer.parseInt(request.getParameter("price"))); //房源价格
			houselist.setIntroduce(request.getParameter("introduce")); //房源介绍
			houselist.setLandlady(username.getUsername()); //房源房东
			houselist.setImg(sqlPath); //房源图片在服务器本地的位置
			houselist.setStatus(request.getParameter("status")); //当前房源的状态
			//将该房源信息插入到数据库
			houselistService.inserthouse(houselist);

			model.addAttribute("houselist", houselist);
			model.addAttribute("mainPage","addhouse.jsp");
			return "admin/main1";
			}
	}

	/**
	 * 跳转到添加房源信息页面 addhouse.jsp （管理员）
	 * @param model
	 * @return
	 */
	@RequestMapping("/toaddhouse")
	public String toaddhoust(Model model){

		model.addAttribute("mainPage","addhouse.jsp");
		return "admin/main1";
	}

	/**
	 * 删除房源 （管理员）
	 * @param id
	 * @return
	 */
	@RequestMapping("/deletehouse")
	public String deletehouse(Integer id){
		//根据id删除房源
		houselistService.deletehouse(id);
		//重定向到展示房源列表页面 作用是删除后更新页面
		return "redirect:ahouselist.action";
	}

	/**
	 * 转发到房源页面 （管理员）
	 * @return
	 */
	@RequestMapping("/toahouselist")
	public String toahouselist(){
		return "ahouselist.action";
	}

	/**
	 * 点击修改按钮根据id查询需要修改的房源 并跳转到修改页面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/findid")
	public String findid(Integer id,Model model){
		Houselist list=houselistService.findid(id);
		model.addAttribute("houselist",list);
		model.addAttribute("mainPage", "changehouse.jsp");
		return "admin/main1";
	}

	/**
	 *  检查房源信息
	 * @param houselist
	 * @param model
	 * @return
	 */
	@RequestMapping("/findhouseidupdate")
	public String findhouseidupdate(Houselist houselist,Model model){
		//判断修改后房源id是否重复
		Houselist list=houselistService.findhouseidupdate(houselist);
		if(list!=null){ //重复
			//返回失败信息
			model.addAttribute("houselist",houselist);
			model.addAttribute("mainPage", "changehouse.jsp");
			model.addAttribute("error","该房屋id已存在");
			return "admin/main1";
		}
		else{ //不重复更新数据库
			houselistService.updatehouse(houselist);
			//返回成功信息
			model.addAttribute("houselist",houselist);
			model.addAttribute("mainPage", "changehouse.jsp");
			model.addAttribute("error","更新成功");
			return "admin/main1";
		}
	}


	/**
	 * 展示指定房源的详情 （租客）
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/houseDetails")
	public String houseDetails(Integer id,Model model){
		Houselist houseDetail=houselistService.findid(id);
		model.addAttribute("houseDetail",houseDetail);
		model.addAttribute("mainPage", "details.jsp");
		return "zuke/main"; 
	}

	/**
	 * 点击提交看房申请 （租客）
	 * @param houselist
	 * @param model
	 * @return
	 */
	@RequestMapping("/changeHouseList")
	public String changeHouseList(Houselist houselist,Model model){
		//System.out.println("传递过来的表单数据为："+houselist.toString());
		model.addAttribute("mainPage", "details.jsp");
		return "zuke/main"; 
	}
	
}
