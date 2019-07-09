package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.BeanNotNull;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 报文体
 * 
 * @author liubin
 * @version 1.0
 * 
 */
public class MultipleVoDatagramBody<T extends Serializable> extends DatagramBody implements Serializable {
	@Valid
	@NotEmpty
	@BeanNotNull
	private List<T> vos = new ArrayList<T>();

	public List<T> getVos() {
		return vos;
	}

	public void setVos(List<T> vos) {
		this.vos = vos;
	}

	public MultipleVoDatagramBody() {
		setMultiple(true);
	}
}
