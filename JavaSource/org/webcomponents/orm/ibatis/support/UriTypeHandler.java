package org.webcomponents.orm.ibatis.support;

import java.net.URI;
import java.sql.SQLException;
import java.sql.Types;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class UriTypeHandler implements TypeHandlerCallback {
	
	@Override
	public Object getResult(ResultGetter getter) throws SQLException {
		String value = getter.getString();
		if(getter.wasNull()) {
			return null;
		}
		return valueOf(value);
	}

	@Override
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if(parameter == null) {
			setter.setNull(Types.VARCHAR);
		} else {
			URI value = (URI) parameter;
			setter.setString(value.toString());
		}
	}

	@Override
	public Object valueOf(String value) {
		if(value == null) {
			return null;
		}
		return URI.create(value);
	}

}
