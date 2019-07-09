package org.springside.modules.orm.hibernate;

import com.zendaimoney.utils.DateFormatUtils;
import com.zendaimoney.utils.DateUtils;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.Date;

/**
 * 日期时间比较
 * 
 * @author larry
 * @since 1.0
 */
public class DateCriterion extends SimpleExpression {

	private static final long serialVersionUID = 5935838736997565333L;
	private static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss.sss";

	protected DateCriterion(String propertyName, Object value, String op) {
		super(propertyName, value, op);
	}

	/**
	 * Apply an "equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression eq(String propertyName, Object value) {
		if (value instanceof Date)
			return new DateCriterion(propertyName, DateUtils.nullSafeParseDate(
					DateFormatUtils.format((Date) value, PATTERN_DATE),
					PATTERN_DATE), "=");
		return Restrictions.eq(propertyName, value);
	}
}
