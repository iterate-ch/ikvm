/*
  Copyright (C) 2002, 2003, 2004 Jeroen Frijters

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  Jeroen Frijters
  jeroen@frijters.net
  
*/
using System;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Runtime.CompilerServices;
using System.Text;
using IKVM.Attributes;
using IKVM.Runtime;
using IKVM.Internal;

using NetSystem = System;

namespace IKVM.NativeCode.java
{
	namespace lang
	{
		namespace reflect
		{
			public class Proxy
			{
				// NOTE not used, only here to shut up ikvmc during compilation of IKVM.GNU.Classpath.dll
				public static object getProxyClass0(object o1, object o2)
				{
					throw new InvalidOperationException();
				}
				
				// NOTE not used, only here to shut up ikvmc during compilation of IKVM.GNU.Classpath.dll
				public static object getProxyData0(object o1, object o2)
				{
					throw new InvalidOperationException();
				}
				
				// NOTE not used, only here to shut up ikvmc during compilation of IKVM.GNU.Classpath.dll
				public static object generateProxyClass0(object o1, object o2)
				{
					throw new InvalidOperationException();
				}
			}

			public class Array
			{
				public static object createObjectArray(object clazz, int dim)
				{
					if(dim >= 0)
					{
						return NetSystem.Array.CreateInstance(VMClass.getWrapperFromClass(clazz).TypeAsArrayType, dim);
					}
					throw JavaException.NegativeArraySizeException();
				}
			}

			internal class JavaWrapper
			{
				private static Type java_lang_Byte = ClassLoaderWrapper.LoadClassCritical("java.lang.Byte").TypeAsTBD;
				private static Type java_lang_Boolean = ClassLoaderWrapper.LoadClassCritical("java.lang.Boolean").TypeAsTBD;
				private static Type java_lang_Short = ClassLoaderWrapper.LoadClassCritical("java.lang.Short").TypeAsTBD;
				private static Type java_lang_Character = ClassLoaderWrapper.LoadClassCritical("java.lang.Character").TypeAsTBD;
				private static Type java_lang_Integer = ClassLoaderWrapper.LoadClassCritical("java.lang.Integer").TypeAsTBD;
				private static Type java_lang_Long = ClassLoaderWrapper.LoadClassCritical("java.lang.Long").TypeAsTBD;
				private static Type java_lang_Float = ClassLoaderWrapper.LoadClassCritical("java.lang.Float").TypeAsTBD;
				private static Type java_lang_Double = ClassLoaderWrapper.LoadClassCritical("java.lang.Double").TypeAsTBD;

				internal static object Box(object o)
				{
					if(o is sbyte)
					{
						return Activator.CreateInstance(java_lang_Byte, new object[] { o });
					}
					else if(o is byte)
					{
						return Activator.CreateInstance(java_lang_Byte, new object[] { (sbyte)(byte)o });
					}
					else if(o is bool)
					{
						return Activator.CreateInstance(java_lang_Boolean, new object[] { o });
					}
					else if(o is short)
					{
						return Activator.CreateInstance(java_lang_Short, new object[] { o });
					}
					else if(o is ushort)
					{
						return Activator.CreateInstance(java_lang_Short, new object[] { (short)(ushort)o });
					}
					else if(o is char)
					{
						return Activator.CreateInstance(java_lang_Character, new object[] { o });
					}
					else if(o is int)
					{
						return Activator.CreateInstance(java_lang_Integer, new object[] { o });
					}
					else if(o is uint)
					{
						return Activator.CreateInstance(java_lang_Integer, new object[] { (int)(uint)o });
					}
					else if(o is long)
					{
						return Activator.CreateInstance(java_lang_Long, new object[] { o });
					}
					else if(o is ulong)
					{
						return Activator.CreateInstance(java_lang_Long, new object[] { (long)(ulong)o });
					}
					else if(o is float)
					{
						return Activator.CreateInstance(java_lang_Float, new object[] { o });
					}
					else if(o is double)
					{
						return Activator.CreateInstance(java_lang_Double, new object[] { o });
					}
					else if(o is Enum)
					{
						Type enumType = Enum.GetUnderlyingType(o.GetType());
						if(enumType == typeof(byte) || enumType == typeof(sbyte))
						{
							return JavaWrapper.Box((sbyte)((IConvertible)o).ToInt32(null));
						}
						else if(enumType == typeof(short) || enumType == typeof(ushort))
						{
							return JavaWrapper.Box((short)((IConvertible)o).ToInt32(null));
						}
						else if(enumType == typeof(int))
						{
							return JavaWrapper.Box(((IConvertible)o).ToInt32(null));
						}
						else if(enumType == typeof(uint))
						{
							return JavaWrapper.Box(unchecked((int)((IConvertible)o).ToUInt32(null)));
						}
						else if(enumType == typeof(long))
						{
							return JavaWrapper.Box(((IConvertible)o).ToInt64(null));
						}
						else if(enumType == typeof(ulong))
						{
							return JavaWrapper.Box(unchecked((long)((IConvertible)o).ToUInt64(null)));
						}
						else
						{
							throw new InvalidOperationException();
						}
					}
					else
					{
						throw new NotImplementedException(o.GetType().FullName);
					}
				}

				internal static object Unbox(object o)
				{
					Type type = o.GetType();
					if(type == java_lang_Byte)
					{
						return java_lang_Byte.GetMethod("byteValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Boolean)
					{
						return java_lang_Boolean.GetMethod("booleanValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Short)
					{
						return java_lang_Short.GetMethod("shortValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Character)
					{
						return java_lang_Character.GetMethod("charValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Integer)
					{
						return java_lang_Integer.GetMethod("intValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Long)
					{
						return java_lang_Long.GetMethod("longValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Float)
					{
						return java_lang_Float.GetMethod("floatValue").Invoke(o, new object[0]);
					}
					else if(type == java_lang_Double)
					{
						return java_lang_Double.GetMethod("doubleValue").Invoke(o, new object[0]);
					}
					else
					{
						throw JavaException.IllegalArgumentException(type.FullName);
					}
				}
			}

			public class Method
			{
				public static String GetName(object methodCookie)
				{
					MethodWrapper wrapper = (MethodWrapper)methodCookie;
					return wrapper.Name;
				}
				
				public static int GetModifiers(object methodCookie)
				{
					MethodWrapper wrapper = (MethodWrapper)methodCookie;
					return (int)wrapper.Modifiers;
				}

				public static object GetReturnType(object methodCookie)
				{
					MethodWrapper wrapper = (MethodWrapper)methodCookie;
					TypeWrapper retType = wrapper.ReturnType;
					// HACK we should have a better plan than this
					if(retType.IsUnloadable)
					{
						retType = wrapper.DeclaringType.GetClassLoader().FieldTypeWrapperFromSig(retType.SigName);
					}
					return VMClass.getClassFromWrapper(retType);
				}

				public static object[] GetParameterTypes(object methodCookie)
				{
					MethodWrapper wrapper = (MethodWrapper)methodCookie;
					TypeWrapper[] parameters = wrapper.GetParameters();
					object[] parameterClasses = new object[parameters.Length];
					for(int i = 0; i < parameters.Length; i++)
					{
						TypeWrapper paramType = parameters[i];
						// HACK we should have a better plan than this
						if(paramType.IsUnloadable)
						{
							paramType = wrapper.DeclaringType.GetClassLoader().FieldTypeWrapperFromSig(paramType.SigName);
						}
						parameterClasses[i] = VMClass.getClassFromWrapper(paramType);
					}
					return parameterClasses;
				}

				public static string[] GetExceptionTypes(object methodCookie)
				{
					try
					{
						MethodWrapper wrapper = (MethodWrapper)methodCookie;
						wrapper.DeclaringType.Finish();
						return wrapper.GetExceptions();
					}
					catch(RetargetableJavaException x)
					{
						throw x.ToJava();
					}
				}

				public static object Invoke(object methodCookie, object o, object[] args)
				{
					try
					{
						object[] argsCopy = new Object[args != null ? args.Length : 0];
						MethodWrapper mw = (MethodWrapper)methodCookie;
						mw.DeclaringType.Finish();
						TypeWrapper[] argWrappers = mw.GetParameters();
						for(int i = 0; i < argWrappers.Length; i++)
						{
							if(argWrappers[i].IsPrimitive)
							{
								if(args[i] == null)
								{
									throw JavaException.IllegalArgumentException("primitive wrapper null");
								}
								argsCopy[i] = JavaWrapper.Unbox(args[i]);
							}
							else
							{
								argsCopy[i] = args[i];
							}
						}
						object retval = mw.Invoke(o, argsCopy, false);
						if(mw.ReturnType.IsPrimitive && mw.ReturnType != PrimitiveTypeWrapper.VOID)
						{
							retval = JavaWrapper.Box(retval);
						}
						return retval;
					}
					catch(RetargetableJavaException x)
					{
						throw x.ToJava();
					}
				}
			}

			public class Field
			{
				// HACK this is used by netexp to query the constant value of a field
				public static object getConstant(object field)
				{
					// HACK we use reflection to extract the fieldCookie from the java.lang.reflect.Field object
					FieldWrapper wrapper = (FieldWrapper)field.GetType().GetField("fieldCookie", BindingFlags.Instance | BindingFlags.NonPublic).GetValue(field);
					return wrapper.GetConstant();
				}

				public static string GetName(object fieldCookie)
				{
					FieldWrapper wrapper = (FieldWrapper)fieldCookie;
					return wrapper.Name;
				}

				public static int GetModifiers(object fieldCookie)
				{
					FieldWrapper wrapper = (FieldWrapper)fieldCookie;
					return (int)wrapper.Modifiers;
				}

				public static object GetFieldType(object fieldCookie)
				{
					FieldWrapper wrapper = (FieldWrapper)fieldCookie;
					TypeWrapper fieldType = wrapper.FieldTypeWrapper;
					// HACK we should have a better plan than this
					if(fieldType.IsUnloadable)
					{
						fieldType = wrapper.DeclaringType.GetClassLoader().FieldTypeWrapperFromSig(fieldType.SigName);
					}
					return VMClass.getClassFromWrapper(fieldType);
				}

				public static bool isSamePackage(object a, object b)
				{
					return VMClass.getWrapperFromClass(a).IsInSamePackageAs(VMClass.getWrapperFromClass(b));
				}

				public static object getClassFromFrame(NetSystem.Diagnostics.StackFrame frame)
				{
					Type type = frame.GetMethod().DeclaringType;
					if(type == null)
					{
						return null;
					}
					return VMClass.getClassFromType(type);
				}

				public static object GetValue(object fieldCookie, object o)
				{
					Profiler.Enter("Field.GetValue");
					try
					{
						FieldWrapper wrapper = (FieldWrapper)fieldCookie;
						object val = wrapper.GetValue(o);
						if(wrapper.FieldTypeWrapper.IsPrimitive)
						{
							val = JavaWrapper.Box(val);
						}
						return val;
					}
					finally
					{
						Profiler.Leave("Field.GetValue");
					}
				}

				public static void SetValue(object fieldCookie, object o, object v)
				{
					Profiler.Enter("Field.SetValue");
					try
					{
						FieldWrapper wrapper = (FieldWrapper)fieldCookie;
						if(wrapper.IsFinal)
						{
							// NOTE Java runs the class initializer when trying to set a final field
							wrapper.DeclaringType.RunClassInit();
							// NOTE even if the caller is the class itself, it still isn't legal
							throw JavaException.IllegalAccessException("Field is final");
						}
						if(wrapper.FieldTypeWrapper.IsPrimitive)
						{
							v = JavaWrapper.Unbox(v);
						}
						wrapper.SetValue(o, v);
					}
					finally
					{
						Profiler.Leave("Field.SetValue");
					}
				}
			}
		}

		public class VMRuntime
		{
			public static string getVersion()
			{
				return typeof(VMRuntime).Assembly.GetName().Version.ToString();
			}

			public static int nativeLoad(string filename)
			{
				return IKVM.Runtime.JniHelper.LoadLibrary(filename);
			}
		}

		public class Math
		{
			public static double pow(double x, double y)
			{
				return NetSystem.Math.Pow(x, y);
			}

			public static double exp(double d)
			{
				return NetSystem.Math.Exp(d);
			}

			public static double rint(double d)
			{
				return NetSystem.Math.Round(d);
			}

			public static double IEEEremainder(double f1, double f2)
			{
				if(double.IsInfinity(f2) && !double.IsInfinity(f1))
				{
					return f1;
				}
				return NetSystem.Math.IEEERemainder(f1, f2);
			}

			public static double sqrt(double d)
			{
				return NetSystem.Math.Sqrt(d);
			}

			public static double floor(double d)
			{
				return NetSystem.Math.Floor(d);
			}

			public static double ceil(double d)
			{
				return NetSystem.Math.Ceiling(d);
			}

			public static double log(double d)
			{
				return NetSystem.Math.Log(d);
			}

			public static double sin(double d)
			{
				return NetSystem.Math.Sin(d);
			}

			public static double asin(double d)
			{
				return NetSystem.Math.Asin(d);
			}

			public static double cos(double d)
			{
				return NetSystem.Math.Cos(d);
			}

			public static double acos(double d)
			{
				return NetSystem.Math.Acos(d);
			}

			public static double tan(double d)
			{
				return NetSystem.Math.Tan(d);
			}

			public static double atan(double d)
			{
				return NetSystem.Math.Atan(d);
			}

			public static double atan2(double y, double x)
			{
				if(double.IsInfinity(y) && double.IsInfinity(x))
				{
					if(double.IsPositiveInfinity(y))
					{
						if(double.IsPositiveInfinity(x))
						{
							return NetSystem.Math.PI / 4.0;
						}
						else
						{
							return NetSystem.Math.PI * 3.0 / 4.0;
						}
					}
					else
					{
						if(double.IsPositiveInfinity(x))
						{
							return - NetSystem.Math.PI / 4.0;
						}
						else
						{
							return - NetSystem.Math.PI * 3.0 / 4.0;
						}
					}
				}
				return NetSystem.Math.Atan2(y, x);
			}
		}

		public class Double
		{
			public static void initIDs()
			{
			}

			public static double parseDouble(string s)
			{
				try
				{
					// TODO I doubt that this is correct
					return double.Parse(s, System.Globalization.CultureInfo.InvariantCulture);
				}
				catch(FormatException x)
				{
					throw JavaException.NumberFormatException(x.Message);
				}
			}

			public static string toString(double d, bool isFloat)
			{
				if(isFloat)
				{
					float f = (float)d;
					// TODO this is not correct, we need to use the Java algorithm of converting a float to string
					if(float.IsNaN(f))
					{
						return "NaN";
					}
					if(float.IsNegativeInfinity(f))
					{
						return "-Infinity";
					}
					if(float.IsPositiveInfinity(f))
					{
						return "Infinity";
					}
					// HACK really lame hack to apprioximate the Java behavior a little bit
					string s = f.ToString(System.Globalization.CultureInfo.InvariantCulture);
					if(s.IndexOf('.') == -1)
					{
						s += ".0";
					}
					// make sure -0.0 renders correctly
					if(d == 0.0 && BitConverter.DoubleToInt64Bits(d) < 0)
					{
						return "-" + s;
					}
					return s;
				}
				else
				{
					StringBuilder sb = new StringBuilder();
					DoubleToString.append(sb, d);
					return sb.ToString();
				}
			}
		}

		public class VMSecurityManager
		{
			public static object getClassContext()
			{
				ArrayList ar = new ArrayList();
				NetSystem.Diagnostics.StackTrace st = new NetSystem.Diagnostics.StackTrace();
				for(int i = 0; i < st.FrameCount; i++)
				{
					NetSystem.Diagnostics.StackFrame frame = st.GetFrame(i);
					// HACK very insecure
					// TODO handle reflection scenario
					if(frame.GetMethod().Name != "getClassContext")
					{
						Type type = frame.GetMethod().DeclaringType;
						if(type != null)
						{
							ar.Add(VMClass.getClassFromType(type));
						}
					}
				}
				return ar.ToArray(CoreClasses.java.lang.Class.Wrapper.TypeAsArrayType);
			}

			public static object currentClassLoader()
			{
				// TODO handle PrivilegedAction
				NetSystem.Diagnostics.StackTrace st = new NetSystem.Diagnostics.StackTrace();
				for(int i = 0; i < st.FrameCount; i++)
				{
					NetSystem.Diagnostics.StackFrame frame = st.GetFrame(i);
					Type type = frame.GetMethod().DeclaringType;
					if(type != null)
					{
						TypeWrapper wrapper = ClassLoaderWrapper.GetWrapperFromTypeFast(type);
						if(wrapper != null && wrapper.GetClassLoader().GetJavaClassLoader() != null)
						{
							return wrapper.GetClassLoader().GetJavaClassLoader();
						}
					}
				}
				return null;
			}
		}

		public class VMSystem
		{
			public static void arraycopy(object src, int srcStart, object dest, int destStart, int len)
			{
				ByteCodeHelper.arraycopy(src, srcStart, dest, destStart, len);
			}

			public static void setErr(object printStream)
			{
				TypeWrapper tw = ClassLoaderWrapper.LoadClassCritical("java.lang.System");
				FieldWrapper fw = tw.GetFieldWrapper("err", ClassLoaderWrapper.LoadClassCritical("java.io.PrintStream"));
				fw.SetValue(null, printStream);
			}

			public static void setIn(object inputStream)
			{
				TypeWrapper tw = ClassLoaderWrapper.LoadClassCritical("java.lang.System");
				FieldWrapper fw = tw.GetFieldWrapper("in", ClassLoaderWrapper.LoadClassCritical("java.io.InputStream"));
				fw.SetValue(null, inputStream);
			}

			public static void setOut(object printStream)
			{
				TypeWrapper tw = ClassLoaderWrapper.LoadClassCritical("java.lang.System");
				FieldWrapper fw = tw.GetFieldWrapper("out", ClassLoaderWrapper.LoadClassCritical("java.io.PrintStream"));
				fw.SetValue(null, printStream);
			}
		}

		public class VMClassLoader
		{
			public static Assembly findResourceAssembly(string name)
			{
				name = JVM.MangleResourceName(name);
				foreach(Assembly asm in AppDomain.CurrentDomain.GetAssemblies())
				{
					if(!(asm is NetSystem.Reflection.Emit.AssemblyBuilder))
					{
						if(asm.GetManifestResourceInfo(name) != null)
						{
							return asm;
						}
					}
				}
				return null;
			}

			public static Assembly[] findResourceAssemblies(string name)
			{
				name = JVM.MangleResourceName(name);
				ArrayList list = new ArrayList();
				foreach(Assembly asm in AppDomain.CurrentDomain.GetAssemblies())
				{
					if(!(asm is NetSystem.Reflection.Emit.AssemblyBuilder))
					{
						if(asm.GetManifestResourceInfo(name) != null)
						{
							list.Add(asm);
						}
					}
				}
				return (Assembly[])list.ToArray(typeof(Assembly));
			}

			public static object loadClass(string name, bool resolve)
			{
				try
				{
					TypeWrapper type = ClassLoaderWrapper.GetBootstrapClassLoader().LoadClassByDottedNameFast(name);
					if(type != null)
					{
						return VMClass.getClassFromWrapper(type);
					}
					return null;
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}

			public static object getBootstrapClassLoader()
			{
				return ClassLoaderWrapper.GetJavaBootstrapClassLoader();
			}

			public static object getPrimitiveClass(char type)
			{
				switch(type)
				{
					case 'Z':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.BOOLEAN);
					case 'B':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.BYTE);
					case 'C':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.CHAR);
					case 'D':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.DOUBLE);
					case 'F':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.FLOAT);
					case 'I':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.INT);
					case 'J':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.LONG);
					case 'S':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.SHORT);
					case 'V':
						return VMClass.getClassFromWrapper(PrimitiveTypeWrapper.VOID);
					default:
						throw new InvalidOperationException();
				}
			}

			public static object defineClass(object classLoader, string name, byte[] data, int offset, int length, object protectionDomain)
			{
				Profiler.Enter("ClassLoader.defineClass");
				try
				{
					try
					{
						ClassFile classFile = new ClassFile(data, offset, length, name, false);
						if(name != null && classFile.Name != name)
						{
							throw new NoClassDefFoundError(name + " (wrong name: " + classFile.Name + ")");
						}
						lock(VMClass.LockObject)
						{
							TypeWrapper type = ClassLoaderWrapper.GetClassLoaderWrapper(classLoader).DefineClass(classFile);
							try
							{
								return VMClass.CreateClassInstance(type, protectionDomain);
							}
							catch(Exception x)
							{
								// this is a critical failure, because otherwise we would open the window to a situation
								// where a Type exists without a Class and then if getClass() was called on an instance
								// of that type, a new Class would be created without the proper protectionDomain
								JVM.CriticalFailure("Unable to create Class", x);
								throw;
							}
						}
					}
					catch(RetargetableJavaException x)
					{
						throw x.ToJava();
					}
				}
				finally
				{
					Profiler.Leave("ClassLoader.defineClass");
				}
			}
		}

		public class VMClass
		{
			private static Hashtable map = new Hashtable();
			private delegate object LookupDelegate(object o);
			private delegate object CreateClassDelegate(object o, object pd);
			private static CreateClassDelegate createClass;
			private static LookupDelegate getWrapper;

			internal static object LockObject
			{
				get
				{
					return map.SyncRoot;
				}
			}

			public static void throwException(Exception e)
			{
				throw e;
			}

			// NOTE when you call this method, you must own the VMClass.LockObject monitor
			internal static object CreateClassInstance(TypeWrapper wrapper, object protectionDomain)
			{
				if(createClass == null)
				{
					TypeWrapper tw = ClassLoaderWrapper.LoadClassCritical("java.lang.VMClass");
					tw.Finish();
					createClass = (CreateClassDelegate)Delegate.CreateDelegate(typeof(CreateClassDelegate), tw.TypeAsTBD.GetMethod("createClass", BindingFlags.Static | BindingFlags.Public));
					// HACK to make sure we don't run into any problems creating class objects for classes that
					// participate in the VMClass static initialization, we first do a bogus call to initialize
					// the machinery (I ran into this when running ikvmstub on IKVM.GNU.Classpath.dll)
					createClass(null, null);
					object o = map[wrapper];
					if(o != null)
					{
						return o;
					}
				}
				object clazz = createClass(wrapper, protectionDomain);
				map.Add(wrapper, clazz);
				return clazz;
			}

			public static bool IsAssignableFrom(object w1, object w2)
			{
				return ((TypeWrapper)w2).IsAssignableTo((TypeWrapper)w1);
			}

			public static bool IsInterface(object wrapper)
			{
				return ((TypeWrapper)wrapper).IsInterface;
			}

			public static bool IsArray(object wrapper)
			{
				return ((TypeWrapper)wrapper).IsArray;
			}

			public static object GetSuperClassFromWrapper(object wrapper)
			{
				TypeWrapper baseWrapper = ((TypeWrapper)wrapper).BaseTypeWrapper;
				if(baseWrapper != null)
				{
					return getClassFromWrapper(baseWrapper);
				}
				return null;
			}

			public static object getComponentClassFromWrapper(object wrapper)
			{
				TypeWrapper typeWrapper = (TypeWrapper)wrapper;
				if(typeWrapper.IsArray)
				{
					return getClassFromWrapper(typeWrapper.ElementTypeWrapper);
				}
				return null;
			}

			public static object loadArrayClass(string name, object classLoader)
			{
				try
				{
					ClassLoaderWrapper classLoaderWrapper = ClassLoaderWrapper.GetClassLoaderWrapper(classLoader);
					TypeWrapper type = classLoaderWrapper.LoadClassByDottedName(name);
					return VMClass.getClassFromWrapper(type);
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}

			internal static TypeWrapper getWrapperFromClass(object clazz)
			{
				if(getWrapper == null)
				{
					TypeWrapper tw = ClassLoaderWrapper.LoadClassCritical("java.lang.VMClass");
					tw.Finish();
					getWrapper = (LookupDelegate)Delegate.CreateDelegate(typeof(LookupDelegate), tw.TypeAsTBD.GetMethod("getWrapperFromClass", BindingFlags.Static | BindingFlags.Public));
				}
				return (TypeWrapper)getWrapper(clazz);
			}

			internal static object getClassFromWrapper(TypeWrapper wrapper)
			{
				NetSystem.Diagnostics.Debug.Assert(!wrapper.IsUnloadable);
				lock(VMClass.LockObject)
				{
					object clazz = map[wrapper];
					if(clazz == null)
					{
						clazz = CreateClassInstance(wrapper, null);
					}
					return clazz;
				}
			}

			public static object getClassFromType(Type type)
			{
				TypeWrapper.AssertFinished(type);
				if(type == null)
				{
					return null;
				}
				return getClassFromWrapper(ClassLoaderWrapper.GetWrapperFromType(type));
			}

			public static string GetName(object wrapper)
			{
				TypeWrapper typeWrapper = (TypeWrapper)wrapper;
				if(typeWrapper.IsPrimitive)
				{
					if(typeWrapper == PrimitiveTypeWrapper.VOID)
					{
						return "void";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.BYTE)
					{
						return "byte";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.BOOLEAN)
					{
						return "boolean";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.SHORT)
					{
						return "short";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.CHAR)
					{
						return "char";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.INT)
					{
						return "int";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.LONG)
					{
						return "long";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.FLOAT)
					{
						return "float";
					}
					else if(typeWrapper == PrimitiveTypeWrapper.DOUBLE)
					{
						return "double";
					}
					else
					{
						throw new InvalidOperationException();
					}
				}
				return typeWrapper.Name;
			}
	
			public static void initialize(object cwrapper)
			{
				TypeWrapper wrapper = (TypeWrapper)cwrapper;
				wrapper.Finish();
				wrapper.RunClassInit();
			}

			public static object getClassLoader0(object wrapper)
			{
				return ((TypeWrapper)wrapper).GetClassLoader().GetJavaClassLoader();
			}

			public static object getClassLoaderFromType(Type type)
			{
				return ClassLoaderWrapper.GetWrapperFromType(type).GetClassLoader().GetJavaClassLoader();
			}

			public static object[] GetDeclaredMethods(object cwrapper, bool getMethods, bool publicOnly)
			{
				Profiler.Enter("VMClass.GetDeclaredMethods");
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					// we need to look through the array for unloadable types, because we may not let them
					// escape into the 'wild'
					MethodWrapper[] methods = wrapper.GetMethods();
					ArrayList list = new ArrayList();
					for(int i = 0; i < methods.Length; i++)
					{
						methods[i].Link();
						// we don't want to expose "hideFromReflection" methods (one reason is that it would
						// mess up the serialVersionUID computation)
						if(!methods[i].IsHideFromReflection)
						{
							if(methods[i].Name == "<clinit>")
							{
								// not reported back
							}
							else if(publicOnly && !methods[i].IsPublic)
							{
								// caller is only asking for public methods, so we don't return this non-public method
							}
							else if((methods[i].Name == "<init>") != getMethods)
							{
								if(methods[i].ReturnType.IsUnloadable)
								{
									// HACK we should have a better plan than this
									if(wrapper.GetClassLoader().LoadClassByDottedNameFast(methods[i].ReturnType.Name) == null)
									{
										throw JavaException.NoClassDefFoundError(methods[i].ReturnType.Name);
									}
								}
								TypeWrapper[] args = methods[i].GetParameters();
								for(int j = 0; j < args.Length; j++)
								{
									if(args[j].IsUnloadable)
									{
										// HACK we should have a better plan than this
										if(wrapper.GetClassLoader().LoadClassByDottedNameFast(args[j].Name) == null)
										{
											throw JavaException.NoClassDefFoundError(args[j].Name);
										}
									}
								}
								list.Add(methods[i]);
							}
						}
					}
					return (MethodWrapper[])list.ToArray(typeof(MethodWrapper));
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
				finally
				{
					Profiler.Leave("VMClass.GetDeclaredMethods");
				}
			}

			public static object[] GetDeclaredFields(object cwrapper, bool publicOnly)
			{
				Profiler.Enter("VMClass.GetDeclaredFields");
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					// we need to finish the type otherwise all fields will not be in the field map yet
					wrapper.Finish();
					FieldWrapper[] fields = wrapper.GetFields();
					if(publicOnly)
					{
						ArrayList list = new ArrayList();
						for(int i = 0; i < fields.Length; i++)
						{
							if(fields[i].IsPublic)
							{
								list.Add(fields[i]);
							}
						}
						fields = (FieldWrapper[])list.ToArray(typeof(FieldWrapper));
					}
					// we need to look through the array for unloadable types, because we may not let them
					// escape into the 'wild'
					for(int i = 0; i < fields.Length; i++)
					{
						if(fields[i].FieldTypeWrapper.IsUnloadable)
						{
							// HACK we should have a better plan than this
							if(wrapper.GetClassLoader().LoadClassByDottedNameFast(fields[i].FieldTypeWrapper.Name) == null)
							{
								throw JavaException.NoClassDefFoundError(fields[i].FieldTypeWrapper.Name);
							}
						}
					}
					return fields;
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
				finally
				{
					Profiler.Leave("VMClass.GetDeclaredFields");
				}
			}

			public static object[] GetDeclaredClasses(object cwrapper, bool publicOnly)
			{
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					// NOTE to get at the InnerClasses we need to finish the type
					wrapper.Finish();
					TypeWrapper[] wrappers = wrapper.InnerClasses;
					if(publicOnly)
					{
						ArrayList list = new ArrayList();
						for(int i = 0; i < wrappers.Length; i++)
						{
							if(wrappers[i].IsUnloadable)
							{
								throw JavaException.NoClassDefFoundError(wrappers[i].Name);
							}
							// because the VM lacks any support for nested visibility control, we
							// cannot rely on the publicness of the type here, but instead we have
							// to look at the reflective modifiers
							wrappers[i].Finish();
							if((wrappers[i].ReflectiveModifiers & Modifiers.Public) != 0)
							{
								list.Add(wrappers[i]);
							}
						}
						wrappers = (TypeWrapper[])list.ToArray(typeof(TypeWrapper));
					}
					object[] innerclasses = new object[wrappers.Length];
					for(int i = 0; i < innerclasses.Length; i++)
					{
						if(wrappers[i].IsUnloadable)
						{
							throw JavaException.NoClassDefFoundError(wrappers[i].Name);
						}
						innerclasses[i] = getClassFromWrapper(wrappers[i]);
					}
					return innerclasses;
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}

			public static object GetDeclaringClass(object cwrapper)
			{
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					// before we can call DeclaringTypeWrapper, we need to finish the type
					wrapper.Finish();
					TypeWrapper declaring = wrapper.DeclaringTypeWrapper;
					if(declaring == null)
					{
						return null;
					}
					if(declaring.IsUnloadable)
					{
						throw JavaException.NoClassDefFoundError(declaring.Name);
					}
					return getClassFromWrapper(declaring);
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}

			public static object[] GetInterfaces(object cwrapper)
			{
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					TypeWrapper[] interfaceWrappers = wrapper.Interfaces;
					object[] interfaces = new object[interfaceWrappers.Length];
					for(int i = 0; i < interfaces.Length; i++)
					{
						interfaces[i] = getClassFromWrapper(interfaceWrappers[i]);
					}
					return interfaces;
				}
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}

			public static int GetModifiers(Object cwrapper)
			{
				try
				{
					TypeWrapper wrapper = (TypeWrapper)cwrapper;
					// NOTE we don't return the modifiers from the TypeWrapper, because for inner classes
					// the reflected modifiers are different from the physical ones
					Modifiers modifiers = wrapper.ReflectiveModifiers;
					// only returns public, protected, private, final, static, abstract and interface (as per
					// the documentation of Class.getModifiers())
					Modifiers mask = Modifiers.Public | Modifiers.Protected | Modifiers.Private | Modifiers.Final |
						Modifiers.Static | Modifiers.Abstract | Modifiers.Interface;
					return (int)(modifiers & mask);
				}				
				catch(RetargetableJavaException x)
				{
					throw x.ToJava();
				}
			}
		}
	}

	namespace io
	{
		public class VMObjectStreamClass
		{
			public static bool hasClassInitializer(object clazz)
			{
				TypeWrapper wrapper = NativeCode.java.lang.VMClass.getWrapperFromClass(clazz);
				wrapper.Finish();
				Type type = wrapper.TypeAsTBD;
				try
				{
					if(!type.IsArray && type.TypeInitializer != null)
					{
						return !AttributeHelper.IsHideFromJava(type.TypeInitializer);
					}
					return false;
				}
				catch(Exception x)
				{
					Console.WriteLine(type.FullName);
					Console.WriteLine(x);
					return false;
				}
			}

			private static FieldWrapper GetFieldWrapperFromField(object field)
			{
				// TODO optimize this
				return (FieldWrapper)field.GetType().GetField("fieldCookie", BindingFlags.NonPublic | BindingFlags.Instance).GetValue(field);
			}

			public static void setDoubleNative(object field, object obj, double val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setFloatNative(object field, object obj, float val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setLongNative(object field, object obj, long val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setIntNative(object field, object obj, int val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setShortNative(object field, object obj, short val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setCharNative(object field, object obj, char val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setByteNative(object field, object obj, sbyte val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setBooleanNative(object field, object obj, bool val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}

			public static void setObjectNative(object field, object obj, object val)
			{
				GetFieldWrapperFromField(field).SetValue(obj, val);
			}
		}

		public class ObjectInputStream
		{
			public static object currentClassLoader(object sm)
			{
				// TODO calling currentClassLoader in SecurityManager results in null being returned, so we use our own
				// version for now, don't know what the security implications of this are
				// SECURITY
				return NativeCode.java.lang.VMSecurityManager.currentClassLoader();
			}

			public static object allocateObject(object ois, object clazz)
			{
				Profiler.Enter("ObjectInputStream.allocateObject");
				try
				{
					TypeWrapper wrapper = NativeCode.java.lang.VMClass.getWrapperFromClass(clazz);
					// if we're trying to deserialize a string as a TC_OBJECT, just return an emtpy string (Sun does the same)
					if(wrapper == CoreClasses.java.lang.String.Wrapper)
					{
						return "";
					}
					wrapper.Finish();
					// TODO do we need error handling? (e.g. when trying to instantiate an interface or abstract class)
					return NetSystem.Runtime.Serialization.FormatterServices.GetUninitializedObject(wrapper.TypeAsBaseType);
				}
				finally
				{
					Profiler.Leave("ObjectInputStream.allocateObject");
				}
			}

			public static void callConstructor(object ois, object clazz, object obj)
			{
				Profiler.Enter("ObjectInputStream.callConstructor");
				try
				{
					TypeWrapper type = NativeCode.java.lang.VMClass.getWrapperFromClass(clazz);
					// if we're trying to deserialize a string as a TC_OBJECT, we already have an initialized emtpy string
					// so there is no need to call the constructor (which wouldn't work anyway).
					if(!(obj is string))
					{
						MethodWrapper mw = type.GetMethodWrapper(new MethodDescriptor("<init>", "()V"), false);
						if(mw == null)
						{
							// TODO what should we do here?
							throw new NotImplementedException();
						}
						// TODO instead of calling link, we should probably Finish the wrapper
						mw.Link();
						// TODO what about exceptions? (should they be unwrapped?)
						mw.Invoke(obj, null, false);
					}
				}
				finally
				{
					Profiler.Leave("ObjectInputStream.callConstructor");
				}
			}
		}
	}

	namespace net
	{
		public class InetAddress
		{
			public static sbyte[] lookupInaddrAny()
			{
				return new sbyte[] { 0, 0, 0, 0 };
			}

			public static string getLocalHostname()
			{
				// TODO error handling
				return NetSystem.Net.Dns.GetHostName();
			}

			public static sbyte[][] getHostByName(string name)
			{
				// TODO error handling
				try
				{
					NetSystem.Net.IPHostEntry he = NetSystem.Net.Dns.GetHostByName(name);
					NetSystem.Net.IPAddress[] addresses = he.AddressList;
					sbyte[][] list = new sbyte[addresses.Length][];
					for(int i = 0; i < addresses.Length; i++)
					{
						byte[] address = addresses[i].GetAddressBytes();
						sbyte[] sb = new sbyte[address.Length];
						for(int j = 0; j < sb.Length; j++)
						{
							sb[j] = (sbyte)address[j];
						}
						list[i] = sb;
					}
					return list;
				}
				catch(Exception x)
				{
					throw JavaException.UnknownHostException(x.Message);
				}
			}

			public static string getHostByAddr(byte[] address)
			{
				string s;
				try
				{
					s = NetSystem.Net.Dns.GetHostByAddress(string.Format("{0}.{1}.{2}.{3}", address[0], address[1], address[2], address[3])).HostName;
				}
				catch(NetSystem.Net.Sockets.SocketException x)
				{
					throw JavaException.UnknownHostException(x.Message);
				}
				try
				{
					NetSystem.Net.Dns.GetHostByName(s);
				}
				catch(NetSystem.Net.Sockets.SocketException)
				{
					// FXBUG .NET framework bug
					// HACK if GetHostByAddress returns a netbios name, it appends the default DNS suffix, but if the
					// machine's netbios name isn't the same as the DNS hostname, this might result in an unresolvable
					// name, if that happens we chop of the DNS suffix.
					int idx = s.IndexOf('.');
					if(idx > 0)
					{
						return s.Substring(0, idx);
					}
				}
				return s;
			}
		}
	}

	namespace security
	{
		public class VMAccessController
		{
			public static object[][] getStack()
			{
				NetSystem.Diagnostics.StackTrace trace = new System.Diagnostics.StackTrace(1);
				object[][] array = new object[2][];
				array[0] = (object[])Array.CreateInstance(CoreClasses.java.lang.Class.Wrapper.TypeAsArrayType, trace.FrameCount);
				array[1] = new string[trace.FrameCount];
				for(int i = 0; i < trace.FrameCount; i++)
				{
					MethodBase mb = trace.GetFrame(i).GetMethod();
					array[0][i] = NativeCode.java.lang.VMClass.getClassFromType(mb.DeclaringType);
					array[1][i] = mb.Name;
				}
				return array;
			}
		}
	}
}

namespace IKVM.NativeCode.gnu.java.net.protocol.ikvmres
{
	public class IkvmresURLConnection
	{
		public static string MangleResourceName(string name)
		{
			return JVM.MangleResourceName(name);
		}
	}
}
