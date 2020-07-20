package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.EmpMapper;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;
import com.xiaoshu.entity.Log;

@Service
public class EmpService {
	@Autowired
	private EmpMapper empMapper;
	public PageInfo<EmpVo> findEmpPage(EmpVo empVo,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<EmpVo> list = empMapper.findEmpPage(empVo);
		PageInfo<EmpVo> page = new PageInfo<>(list);
		return page;
	}
	public void deleteEmp(int parseInt) {
		
		empMapper.deleteByPrimaryKey(parseInt);
		
	}
	public Emp findByName(String tbEmpName) {
		Emp emp = new Emp();
		emp.setTbEmpName(tbEmpName);
		Emp emp2 = empMapper.selectOne(emp);
		return emp2;
	}
	public void updateEmp(Emp emp) {
		empMapper.updateByPrimaryKeySelective(emp);
		
	}
	public void addEmp(Emp emp) {
		empMapper.insert(emp);
		
	}
	public List<EmpVo> findList(EmpVo empVo) {
		List<EmpVo> list = empMapper.findEmpPage(empVo);
		return list;
	}
	public int findeid(String ename) {
		// TODO Auto-generated method stub
		return empMapper.findeid(ename);
	}


}
