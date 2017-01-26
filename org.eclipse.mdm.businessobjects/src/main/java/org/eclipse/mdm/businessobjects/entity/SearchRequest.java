package org.eclipse.mdm.businessobjects.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchRequest {
	List<String> sources;
	String filter;
	String type;
	List<ColumDefinition> returnColumns;
	
	public SearchRequest() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getSources() {
		return sources;
	}

	public String getFilter() {
		return filter;
	}

	public String getType() {
		return type;
	}

	public List<ColumDefinition> getReturnColumns() {
		return returnColumns;
	}
	
	
}
