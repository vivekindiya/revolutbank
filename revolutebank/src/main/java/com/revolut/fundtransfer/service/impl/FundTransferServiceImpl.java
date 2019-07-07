package com.revolut.fundtransfer.service.impl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.fundtransfer.dao.DBDAOFactory;
import com.revolut.fundtransfer.dao.H2DAOFactory;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.UserTransaction;
import com.revolut.fundtransfer.utils.MoneyUtil;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class FundTransferServiceImpl {

	private final DBDAOFactory daoFactory = H2DAOFactory.getDAOFactory(H2DAOFactory.H2DB);
	
	/**
	 * Transfer funds
	 * @param transaction
	 * @return
	 * @throws HSQLDBException
	 */
	@POST
	public Response transferFund(UserTransaction transaction) throws HSQLDBException {

		String currency = transaction.getCurrencyCode();
		if (MoneyUtil.INSTANCE.validateCcyCode(currency)) {
			int updateCount = daoFactory.getAccountDAO().transferAccountBalance(transaction);
			if (updateCount == 2) {
				return Response.status(Response.Status.OK).build();
			} else {
				// transaction failed
				throw new WebApplicationException("Transaction failed : ", Response.Status.BAD_REQUEST);
			}

		} else {
			throw new WebApplicationException("Invalid currency code : ", Response.Status.BAD_REQUEST);
		}

	}
}
