package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.entity.Category;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentVo;

@Service
public class ContentService {
	@Autowired
	private ContentMapper contentMapper;

	public PageInfo<ContentVo> findList(ContentVo vo, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<ContentVo> clist = contentMapper.findList(vo);
		PageInfo<ContentVo> page = new PageInfo<>(clist);
		return page;
	}

	public Content findByName(String contenttitle) {
		Content con = new Content();
		con.setContenttitle(contenttitle);
		Content con1 = contentMapper.selectOne(con);
		return con1;
	}

	public void addContent(Content content) {
		contentMapper.insert(content);
		
	}

	public void updateContent(Content content) {
		contentMapper.updateByPrimaryKeySelective(content);
		
	}

	public void deleteUser(int parseInt) {
		
		contentMapper.deleteByPrimaryKey(parseInt);
		
	}



}
