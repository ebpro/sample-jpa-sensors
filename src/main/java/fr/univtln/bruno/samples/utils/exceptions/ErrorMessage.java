package fr.univtln.bruno.samples.utils.exceptions;

import fr.univtln.bruno.samples.utils.AppConstants;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
@Builder
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessage {

    private String link;
    /**
     * contains the same HTTP Status code returned by the server
     */
    private int status;

    /**
     * application specific error code
     */
    private AppConstants.ErrorCode code;

    /**
     * message describing the error
     */
    private String message;

    private Map<String, String> messageDetail;

    public ErrorMessage(AppException ex) {
        try {
            BeanUtils.copyProperties(this, ex);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
