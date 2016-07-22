package org.eclipse.mdm.property;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Properties;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

public class PropertyServiceTest {

	public static final String TEST_BEAN_PROPERTY_KEY = "key_bean";
	public static final String TEST_BEAN_PROPERTY_VALUE = "value_bean";

	public static final String TEST_GLOBAL_PROPERTY_KEY = "key_global";
	public static final String TEST_GLOBAL_PROPERTY_VALUE = "value_global";

	@Test
	public void testGetBeanPropertyValue() throws Exception {
		PropertyService propertyService = createPropertyServiceMock(true);
		InjectionPoint injectionPointMock = createInjectionPointMock();
		String beanPropValue = propertyService.getBeanPropertyValue(injectionPointMock);
		assertEquals("The bean property value should be: " + TEST_BEAN_PROPERTY_VALUE, beanPropValue,
				TEST_BEAN_PROPERTY_VALUE);
	}

	@Test(expected = PropertyException.class)
	public void testGetUnknownBeanPropertyValue() throws Exception {
		PropertyService propertyService = createPropertyServiceMock(false);
		InjectionPoint injectionPointMock = createInjectionPointMock();
		propertyService.getBeanPropertyValue(injectionPointMock);
		// A PropertyException should be thrown here
	}

	@Test
	public void testGetGlobalPropertyValue() throws Exception {
		PropertyService propertyService = createPropertyServiceMock(true);
		InjectionPoint injectionPointMock = createInjectionPointMock();
		String beanPropValue = propertyService.getGlobalPropertyValue(injectionPointMock);
		assertEquals("The bean property value should be: " + TEST_GLOBAL_PROPERTY_VALUE, beanPropValue,
				TEST_GLOBAL_PROPERTY_VALUE);
	}

	@Test(expected = PropertyException.class)
	public void testGetUnknownGlobalPropertyValue() throws Exception {
		PropertyService propertyService = createPropertyServiceMock(false);
		InjectionPoint injectionPointMock = createInjectionPointMock();
		propertyService.getGlobalPropertyValue(injectionPointMock);
		// A PropertyException should be thrown here
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private InjectionPoint createInjectionPointMock() {
		InjectionPoint injectionPointMock = Mockito.mock(InjectionPoint.class);
		Member memberMock = Mockito.mock(Member.class);
		when(memberMock.getName()).thenReturn(TEST_BEAN_PROPERTY_KEY);
		Class class1 = PropertyServiceTest.class;
		when(memberMock.getDeclaringClass()).thenReturn(class1);
		when(injectionPointMock.getMember()).thenReturn(memberMock);
		Bean beanMock = Mockito.mock(Bean.class);
		when(beanMock.getBeanClass()).thenReturn(PropertyServiceTest.class);
		when(injectionPointMock.getBean()).thenReturn(beanMock);

		Annotated annotatedMock = Mockito.mock(Annotated.class);
		GlobalProperty globalPropertyMock = Mockito.mock(GlobalProperty.class);
		when(globalPropertyMock.value()).thenReturn(TEST_GLOBAL_PROPERTY_KEY);
		when(annotatedMock.getAnnotation(GlobalProperty.class)).thenReturn(globalPropertyMock);
		when(injectionPointMock.getAnnotated()).thenReturn(annotatedMock);
		return injectionPointMock;
	}

	private PropertyService createPropertyServiceMock(boolean initProperies) throws Exception {
		PropertyService propertyService = new PropertyService();
		Field beanPropsField = propertyService.getClass().getDeclaredField("beanProperties");
		beanPropsField.setAccessible(true);
		Properties beanProps = new Properties();
		if (initProperies) {
			beanProps.setProperty(PropertyServiceTest.class.getName() + "." + TEST_BEAN_PROPERTY_KEY,
					TEST_BEAN_PROPERTY_VALUE);
		}
		beanPropsField.set(propertyService, beanProps);
		beanPropsField.setAccessible(false);

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
