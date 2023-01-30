package fr.univtln.bruno.samples.utils.dao.exceptions;

import fr.univtln.bruno.samples.utils.AppConstants;

/**
 * Created by bruno on 04/12/14.
 */
public class PageNotFoundException extends DAOException {
    public PageNotFoundException() {
        super(AppConstants.ErrorCode.DAO_EXCEPTION, "Page doesn't exist", "Page doesn't exist", null);
    }
}
