package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import Pojo.Houselist;
import Pojo.Schedule;
import service.ScheduleService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	@Autowired
	private ScheduleService scheduleService;

	/**
	 * 管理员查看日程
	 * 例： 今天给张三收租
	 * @param model
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/selectAll")
	public String selectAll(Model model ,@RequestParam(required=false,defaultValue="1") Integer page,
            @RequestParam(required=false,defaultValue="6") Integer pageSize){
		 PageHelper.startPage(page, pageSize);
		List<Schedule> schedule=scheduleService.selectAll();
		PageInfo<Schedule> p=new PageInfo<Schedule>(schedule);
		model.addAttribute("schedule", schedule);
		model.addAttribute("p", p);
		model.addAttribute("mainPage", "schedule.jsp");
		return "admin/main1";
	}

	/**
	 * 管理员删除日程
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteschedule")
	public String deleteschedule(Integer id){
		scheduleService.deleteschedule(id);
		return "redirect:selectAll.action";
	}

	/**
	 * 点击提交 创建新日程
	 * @param schedule
	 * @param model
	 * @return
	 */
	@RequestMapping("/insertschedule")
	public String insertschedule(Schedule schedule,Model model){
		scheduleService.insertschedule(schedule);
		
		return "redirect:selectAll.action";
		
	}

	/**
	 * 管理员修改日程
	 * @param schedule
	 * @param model
	 * @return
	 */
	@RequestMapping("/updateschedule")
	public String updateschedule(Schedule schedule,Model model){
		scheduleService.updateschedule(schedule);
		model.addAttribute("error", "更新成功");
		model.addAttribute("schedule", schedule);
		model.addAttribute("mainPage", "updateschedule.jsp");
		return "admin/main1";
		
	}

	/**
	 * 跳转到创建日程界面
	 * @param model
	 * @return
	 */
	@RequestMapping("/toinsert")
	public String toinsert(Model model){
		model.addAttribute("mainPage", "addschedule.jsp");
		
		return "admin/main1";
		
	}

	/**
	 * 跳转到更新日程界面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/toupdate")
	public String toupdate(Model model,Integer id){
		Schedule schedule=scheduleService.selectbyid(id);
		model.addAttribute("schedule", schedule);
		
		model.addAttribute("mainPage", "updateschedule.jsp");
		
		return "admin/main1";
		
	}
}
