package org.eclipse.mdm.property;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Properties;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Test;
import org.mockito.Mockito;

public class PropertyServiceTest {

	public static final String TEST_GLOBAL_PROPERTY_KEY = "key_global";
	public static final String TEST_GLOBAL_PROPERTY_VALUE = "value_global";

	@Test
	public void testGetGlobalPropertyValue() throws Exception {
		GlobalPropertyService propertyService = createPropertyServiceMock(true);
		InjectionPoint injectionPointMock = createInjectionPointMock();
		String beanPropValue = propertyService.getGlobalPropertyValue(injectionPointMock);
		assertEquals("The bean property value should be: " + TEST_GLOBAL_PROPERTY_VALUE, beanPropValue,
				TEST_GLOBAL_PROPERTY_VALUE);
	}


	private InjectionPoint createInjectionPointMock() {
		InjectionPoint injectionPointMock = Mockito.mock(InjectionPoint.class);
		Annotated annotatedMock = Mockito.mock(Annotated.class);
		GlobalProperty globalPropertyMock = Mockito.mock(GlobalProperty.class);
		when(globalPropertyMock.value()).thenReturn(TEST_GLOBAL_PROPERTY_KEY);
		when(annotatedMock.getAnnotation(GlobalProperty.class)).thenReturn(globalPropertyMock);
		when(injectionPointMock.getAnnotated()).thenReturn(annotatedMock);
		return injectionPointMock;
	}

	
	private GlobalPropertyService createPropertyServiceMock(boolean initProperies) throws Exception {
		GlobalPropertyService propertyService = new GlobalPropertyService();
		Field globalPropsField = propertyService.getClass().getDeclaredField("globalProperties");
		globalPropsField.setAccessible(true);
		Properties globalProps = new Properties();
		if (initProperies) {
			globalProps.setProperty(TEST_GLOBAL_PROPERTY_KEY, TEST_GLOBAL_PROPERTY_VALUE);
		}
		globalPropsField.set(propertyService, globalProps);
		globalPropsField.setAccessible(false);
		return propertyService;
	}

}
