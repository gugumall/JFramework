package j.security;

import j.service.server.ServiceBaseImpl;

import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public abstract class VerifyCodeServiceAbstract extends ServiceBaseImpl implements VerifyCodeService,Serializable {
	private static final long serialVersionUID = 1L;

	public VerifyCodeServiceAbstract() {
		super();
	}
}