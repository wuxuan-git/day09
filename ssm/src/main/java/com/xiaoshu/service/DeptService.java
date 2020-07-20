package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.DeptVo;

@Service
public class DeptService {

	@Autowired
	DeptMapper deptMapper;
	public PageInfo<Dept> findPage(DeptVo deptVo,int pageNum,int pageSize,String ordername,String order){
		PageHelper.startPage(pageNum, pageSize);
		//List<Dept> list = deptMapper.select(dept);
		ordername = StringUtil.isNotEmpty(ordername)?ordername:"id";
		order = StringUtil.isNotEmpty(order)?order:"desc";
		deptVo.setOrderByClause(ordername+" "+order); //id desc
		List<Dept> list = deptMapper.findDeptList(deptVo);
		PageInfo<Dept> page = new PageInfo<>(list);
		return page;
		
	}
	public Dept findByName(String name) {
		Dept dept = new Dept();
		dept.setName(name);
		Dept dept1 = deptMapper.selectOne(dept);
		return dept1;
	}
	public void addDser(Dept dept) {
		deptMapper.insert(dept);
		
	}
	public void deleteDept(int parseInt) {
		deptMapper.deleteByPrimaryKey(parseInt);
		
	}
	public void updateDept(Dept dept) {
		deptMapper.updateByPrimaryKeySelective(dept);
		
	}
	public List<Dept> findDept() {
		
		return deptMapper.selectAll();
	}




}
