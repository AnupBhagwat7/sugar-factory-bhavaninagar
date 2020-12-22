/**
 * 
 */
package com.sugarfactory.validator;

/**
 * @author Administrator
 *
 */
public class CustomValidatorEntity {

	private Integer distance;
  
	private Integer actualDistance;
  
	public Boolean existManagerByDepartmentId(Long departmentId) {
		return false;
	}
  
	public Boolean existEmployeeWithMail(String email) {
		return true;
	}
  
}
