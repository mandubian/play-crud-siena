package play.modules.crudsiena;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.data.binding.BeanWrapper;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.UnexpectedException;
import play.modules.siena.Model;
import siena.Id;
import siena.SienaException;

public class SienaUtils {
	public static <T extends Model> T addListElement(T o, String fieldName) {
    	try {
    		Class<? extends Model> clazz = o.getClass();
			BeanWrapper bw = new BeanWrapper(o.getClass());
			Field field = clazz.getField(fieldName);
			
			if(List.class.isAssignableFrom(field.getType())){
				List l = (List)field.get(o);
				if(l == null)
					l = new ArrayList();
				
				Class<?> embedClass = 
					(Class<?>) ((ParameterizedType) 
							field.getGenericType()).getActualTypeArguments()[0];
				BeanWrapper embedbw = new BeanWrapper(embedClass);
				Object embedObj = createObjectInstance(embedClass);
				
				l.add(embedObj);
				
				Logger.debug(embedObj.toString());
				
				bw.set(field.getName(), o, l);			
			}
			else Validation.addError(
					clazz.getName() + "."+field.getName(), 
					"validation.fieldList.badType", fieldName);			
			
			o.update();
			return (T) o;
    	} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public static <T extends Model> T deleteListElement(T o, String fieldName, int idx) {
    	try {
    		Class<?> clazz = o.getClass();
			BeanWrapper bw = new BeanWrapper(o.getClass());
			Field field = clazz.getField(fieldName);
			
			if(List.class.isAssignableFrom(field.getType())){
				List l = (List)field.get(o);
				if(l == null)
					Validation.addError(
							clazz.getName() + "."+field.getName(), 
							"validation.fieldList.empty", fieldName);
				else {
					if(idx < 0 || idx > l.size()-1)
						Validation.addError(
							clazz.getName() + "."+field.getName(), 
							"validation.fieldList.indexOutOfBound", fieldName);
					else l.remove(idx);
				}
			}
			else Validation.addError(
					clazz.getName() + "."+field.getName(), 
					"validation.fieldList.badType", fieldName);

			o.update();
			
    		return (T) o;
    	} catch (Exception e) {
			throw new UnexpectedException(e);
		}
    }
	
	public static <T extends Model> T addMapElement(T o, String fieldName, String key) 
    {
    	try {
    		Class<?> clazz = o.getClass();
			BeanWrapper bw = new BeanWrapper(o.getClass());
			Field field = clazz.getField(fieldName);
			
			if(Map.class.isAssignableFrom(field.getType())){
				Map l = (Map)field.get(o);
								
				Class<?> embedKeyClass = 
					(Class<?>) ((ParameterizedType) 
							field.getGenericType()).getActualTypeArguments()[0];
				
				Class<?> embedClass = 
					(Class<?>) ((ParameterizedType) 
						field.getGenericType()).getActualTypeArguments()[1];
					
				if(l == null){
					l = new HashMap();
				}

				Object embedObj = embedClass.newInstance();
				Object embedKey = Binder.directBind(key, embedKeyClass);
				
				if(l.get(embedKey) != null){
					Logger.debug("element with key %s already existing", embedKey);
					Validation.addError(
							fieldName, 
							"validation.fieldMap.alreadyExists", embedKey.toString());	
				}
				else {
					l.put(embedKey, embedObj);
					Logger.debug("map added {%s:%s}", embedKey, embedObj);
				}		
				
				bw.set(field.getName(), o, l);			
			}
			else Validation.addError(
					clazz.getName() + "."+field.getName(), 
					"validation.fieldMap.badType", fieldName);			
			
			o.update();
			return (T) o;
    	} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public static <T extends Model> T deleteMapElement(T o, String fieldName, String key)
    {
    	try {
    		Class<?> clazz = o.getClass();
			BeanWrapper bw = new BeanWrapper(o.getClass());
			Field field = clazz.getField(fieldName);
			
			if(Map.class.isAssignableFrom(field.getType())){
				Map l = (Map)field.get(o);
				if(l == null)
					Validation.addError(
							clazz.getName() + "."+field.getName(), 
							"validation.fieldMap.empty", fieldName);
				else {
					Class<?> embedKeyClass = 
						(Class<?>) ((ParameterizedType) 
								field.getGenericType()).getActualTypeArguments()[0];
					BeanWrapper keybw = new BeanWrapper(embedKeyClass);
					try {
						Object embedKey = Binder.directBind(key, embedKeyClass);
						l.remove(embedKey);
					}catch(Exception ex){
						Validation.addError(
							clazz.getName() + "."+field.getName() + "." + key, 
							"validation.fieldMap.keyBadFormat", fieldName, key);
					}					
				}
			}
			else Validation.addError(
					clazz.getName() + "."+field.getName(), 
					"validation.fieldMap.badType", fieldName);

			o.update();
			
    		return (T) o;
    	} catch (Exception e) {
			throw new UnexpectedException(e);
		}
    }
	
    // More utils
    public static Object findKey(Object entity) {
        try {
            Class<?> c = entity.getClass();
            while (!c.equals(Object.class)) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field.get(entity);
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error while determining the object @Id for an object of type " + entity.getClass());
        }
        return null;
    }    
    
 	public static Class<?> findKeyType(Class<?> c) {
        try {
            while (!c.equals(Object.class)) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field.getType();
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error while determining the object @Id for an object of type " + c);
        }
        return null;
    }
 	
 	public static String findKeyName(Class<?> c) {
        try {
            while (!c.equals(Object.class)) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field.getName();
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error while determining the object @Id for an object of type " + c);
        }
        return null;
    }

	/**
	 * Creates an instance of a class.
	 * It tries to find a default constructor and if not found, it uses class.newInstance()
	 * 
	 * @param clazz the class
	 * @return the instance
	 */
	public static Object createObjectInstance(Class<?> clazz){
		try {
			Constructor<?> c = clazz.getDeclaredConstructor();
			c.setAccessible(true);
			return c.newInstance();
		}catch(NoSuchMethodException ex){
			try {
				return clazz.newInstance();
			}catch (Exception e) {
				throw new SienaException(e);
			}
		}catch(Exception e){
			throw new SienaException(e);
		}		
	}
 	
}