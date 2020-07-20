package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.DeptService;
import com.xiaoshu.service.EmpService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("emp")
public class EmpController extends LogController{
	static Logger logger = Logger.getLogger(EmpController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	@Autowired
	private EmpService empService;
	@Autowired
	private DeptService deptService;
	
	@RequestMapping("empIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<Dept> dlist = deptService.findDept();
		//查询菜单信息把部门信息查找出来
		request.setAttribute("dlist", dlist);
		return "emp";
	}
	
	
	@RequestMapping(value="empList",method=RequestMethod.POST)
	public void userList(EmpVo empVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {

			/*String order = request.getParameter("order");
			String ordername = request.getParameter("ordername");*/

			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<EmpVo> empList= empService.findEmpPage(empVo,pageNum,pageSize);
			

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",empList.getTotal() );
			jsonObj.put("rows", empList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(MultipartFile picFile,HttpServletRequest request,Emp emp,HttpServletResponse response) throws Exception, Exception{
		Integer id = emp.getTbEmpId();
		JSONObject result=new JSONObject();
		//判断图片是否为空
		if(picFile!=null && picFile.getSize()>0){
			//获取图片名称
			String filename = picFile.getOriginalFilename();
			//获取图片的后缀名
			String suffix = filename.substring(filename.lastIndexOf("."));
			//重命名
			String newfilename = System.currentTimeMillis()+suffix;
			//配置虚拟路径
			File file = new File("d:/img/"+newfilename);
			//上传图片
			picFile.transferTo(file);
			emp.setTbEmpImg(newfilename);
		}
		try {
			//去重
			Emp emp2 = empService.findByName(emp.getTbEmpName());
			if (id != null) {   // userId不为空 说明是修改
				//User userName = userService.existUserWithUserName(user.getUsername());
				if(emp2 != null || (emp2!=null && emp2.getTbEmpId().equals(id))){
					empService.updateEmp(emp);
					//user.setUserid(userId);
					
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该员工姓名被使用");
				}
				
			}else {   // 添加
				if(emp2==null){  // 没有重复可以添加
					empService.addEmp(emp);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该员工姓名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	//删除
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				//userService.deleteUser(Integer.parseInt(id));
				empService.deleteEmp(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除人员信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	//导出
	@RequestMapping("exportEmp")
	public void exportEmp(EmpVo empVo,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "手动备份"+time;
			
			List<EmpVo> list = empService.findList(empVo);
		
			String[] handers = {"员工编号","员工姓名人","性别","年龄","住址","头像","出生日期","部门"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			result.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导出人员信息错误",e);
			result.put("errorMsg", "对不起，导出失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	// 导出到硬盘
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<EmpVo> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
			    //获取list里面存在是数据集对象
			    EmpVo empVo = list.get(i);
			    //创建数据行
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    //"序号","操作人","IP地址","操作时间","操作模块","操作类型","详情"

			    row.createCell(0).setCellValue(empVo.getTbEmpId());
			    row.createCell(1).setCellValue(empVo.getTbEmpName());
			    row.createCell(2).setCellValue(empVo.getTbEmpSex().equals("1")?"男":"女");
			    row.createCell(3).setCellValue(empVo.getTbEmpAge());
			    row.createCell(4).setCellValue(empVo.getTbEmpAddress());
			    row.createCell(5).setCellValue(empVo.getTbEmpImg());
			    row.createCell(6).setCellValue(TimeUtil.formatTime(empVo.getTbEmpBirthday(),"yyyy-MM-dd"));
			    row.createCell(7).setCellValue(empVo.getDname());
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("d://"+excleName+".xls");
						
						
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
	            } 
				os = new FileOutputStream(file);
				wb.write(os);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}
	
	@SuppressWarnings("inload")
	private void inload(MultipartFile inloadfile,HttpServletRequest request,String[] handers, List<EmpVo> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook(inloadfile.getInputStream());//创建工作簿
			HSSFSheet sheet = wb.getSheetAt(0);
			int num = sheet.getLastRowNum();
			for (int i = 1; i <= num; i++) {
				HSSFRow row = sheet.getRow(i);
				String tbEmpName = row.getCell(0).getStringCellValue();
				String tbEmpSex = row.getCell(0).getStringCellValue();
				String tbEmpAge = row.getCell(0).getStringCellValue();
				String tbEmpAddress = row.getCell(0).getStringCellValue();
				String tbEmpImg = row.getCell(0).getStringCellValue();
				String bir = row.getCell(0).getStringCellValue();
				Date tbEmpBirthday = TimeUtil.ParseTime(bir, "yyyy-MM-dd");
				String ename = row.getCell(0).getStringCellValue();
				int tbEmpDid=empService.findeid(ename);
				//Emp emp = new Emp(tbEmpName, tbEmpSex, tbEmpAge, tbEmpAddress, tbEmpImg, tbEmpBirthday, tbEmpDid);
				//new Emp(tbEmpName, tbEmpSex, tbEmpAge, tbEmpAddress, tbEmpImg, tbEmpBirthday, tbEmpDid);
			}
			/*rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
				//获取第一行的每一个单元格
				HSSFCell cell = rowFirst.createCell(i);
				//往单元格里面写入值
				cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
				//获取list里面存在是数据集对象
				EmpVo empVo = list.get(i);
				//创建数据行
				HSSFRow row = sheet.createRow(i+1);
				//设置对应单元格的值
				row.setHeight((short)400);   // 设置每行的高度
				//"序号","操作人","IP地址","操作时间","操作模块","操作类型","详情"
				
				row.createCell(0).setCellValue(empVo.getTbEmpId());
				row.createCell(1).setCellValue(empVo.getTbEmpName());
				row.createCell(2).setCellValue(empVo.getTbEmpSex().equals("1")?"男":"女");
				row.createCell(3).setCellValue(empVo.getTbEmpAge());
				row.createCell(4).setCellValue(empVo.getTbEmpAddress());
				row.createCell(5).setCellValue(empVo.getTbEmpImg());
				row.createCell(6).setCellValue(TimeUtil.formatTime(empVo.getTbEmpBirthday(),"yyyy-MM-dd"));
				row.createCell(7).setCellValue(empVo.getDname());
			}
			//写出文件（path为文件路径含文件名）
			OutputStream os;
			File file = new File("d://"+excleName+".xls");
			
			
			
			if (!file.exists()){//若此目录不存在，则创建之  
				file.createNewFile();  
				logger.debug("创建文件夹路径为："+ file.getPath());  
			} 
			os = new FileOutputStream(file);
			wb.write(os);
			os.close();*/
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
