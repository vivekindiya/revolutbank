package com.revolut.fundtransfer.service;

import javax.ws.rs.core.Response;

import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.UserTransaction;

/**
 * Interface to transfer fund between account
 * @author Vivek Shah
 *
 */
public interface FundTransferService {
	public Response transferFund(UserTransaction transaction) throws HSQLDBException;
}
