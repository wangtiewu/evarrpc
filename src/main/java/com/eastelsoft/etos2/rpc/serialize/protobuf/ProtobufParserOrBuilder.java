package com.eastelsoft.etos2.rpc.serialize.protobuf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.proxy.ReflectionUtils;
import com.eastelsoft.etos2.rpc.tool.JacksonUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DynamicMessage.Builder;
import com.google.protobuf.Message;

public class ProtobufParserOrBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(ProtobufParserOrBuilder.class);
	private Map<String, Descriptor> descriptors = null;
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	public static final String PROTOC_PATH = System.getProperty("protoc") == null ? "protoc"
			: System.getProperty("protoc");
	private File descFile;
	private Cache<String, Class> cache = CacheBuilder.newBuilder()
			.maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

	public ProtobufParserOrBuilder() {
		descriptors = new HashMap<String, Descriptor>();
	}

	public ProtobufParserOrBuilder(File proto) {
		descriptors = new HashMap<String, Descriptor>();
		init(proto);
	}

	private void init(File proto) {
		logger.info("init proto file: " + proto.getAbsolutePath());
		if (descFile != null && descFile.exists()) {
			descFile.delete();
		}
		this.descFile = createDescripFile(proto);
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(descFile);
			FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);
			for (FileDescriptorProto fdp : descriptorSet.getFileList()) {
				FileDescriptor fd = FileDescriptor.buildFrom(fdp,
						new FileDescriptor[] {});
				for (Descriptor descriptor : fd.getMessageTypes()) {
					String className = descriptor.getName();
					this.descriptors.put(className, descriptor);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DescriptorValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private File createDescripFile(File proto) {
		InputStream in = null;
		BufferedReader br = null;
		String lsErrInfoLine = "";
		byte[] lbErrInfoLine = null;
		OutputStream outputStream = null;
		try {
			Runtime run = Runtime.getRuntime();
			String descFileName = System.currentTimeMillis()
					+ "FastProtoParser.desc";
			String protoPath = proto.getCanonicalPath();
			String protoFPath = new File(protoPath).getParentFile() == null ? "."
					: new File(protoPath).getParentFile().getAbsolutePath();
			String cmd = PROTOC_PATH + " -I=" + protoFPath
					+ " --descriptor_set_out=" + TEMP_DIR + descFileName + " "
					+ protoPath;
			logger.info(cmd);
			// 如果不正常终止, 则生成desc文件失败
			Process proc = run.exec(cmd);
			in = proc.getErrorStream();// 得到错误信息输出。
			br = new BufferedReader(new InputStreamReader(in));
			while ((lsErrInfoLine = br.readLine()) != null) {
				if (outputStream != null) {
					lbErrInfoLine = lsErrInfoLine.getBytes();
					outputStream.write(lbErrInfoLine);
				} else {
					logger.error(lsErrInfoLine);
				}
			}
			if (proc.waitFor() != 0) {
				if (proc.exitValue() == 1) {// p.exitValue()==0表示正常结束，1：非正常结束
					throw new RuntimeException("protoc 编译器报错");
				}
			}
			return new File(TEMP_DIR + descFileName);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br = null;
			}
		}
		return null;

	}

	/**
	 * 从protobuf字节数据中,得到指定类型的对象实例.
	 * 
	 * @param clazz
	 * @param bytes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T decode(Class<T> clazz, byte[] bytes) throws Exception {
		String simpleName = clazz.getSimpleName();
		Descriptor desc = this.descriptors.get(simpleName);
		return (T) parseMessage(clazz.getName(),
				DynamicMessage.parseFrom(desc, bytes));
	}

	private Object parseMessage(final String className, DynamicMessage message)
			throws Exception {
		Map<String, Object> fields = new HashMap<String, Object>();
		Map<FieldDescriptor, Object> fieldDescs = message.getAllFields();
		for (Map.Entry<FieldDescriptor, Object> entry : fieldDescs.entrySet()) {
			if (ReflectionUtils
					.isPrimitiveOrString(entry.getValue().getClass())) {// 基础类型和String
				fields.put(entry.getKey().getName(), entry.getValue()
						.toString());
			} else if (entry.getValue().getClass() == DynamicMessage.class) {// 消息类型
				DynamicMessage subMessage = (DynamicMessage) entry.getValue();
				String subClassName = subMessage.getDescriptorForType()
						.getName();
				fields.put(entry.getKey().getName(),
						parseMessage(subClassName, subMessage));

			} else if (entry
					.getValue()
					.getClass()
					.getName()
					.equals("java.util.Collections$UnmodifiableRandomAccessList")) {// 其他类型属性
				int count = message.getRepeatedFieldCount(entry.getKey());
				Object[] params = new Object[count];
				for (int i = 0; i < count; i++) {
					Object param = message.getRepeatedField(entry.getKey(), i);
					ByteString byteString = (ByteString) param;
					params[i] = new String(byteString.toByteArray());
				}
				fields.put(entry.getKey().getName(), params);
			} else if (entry.getValue().getClass().getName()
					.equals("com.google.protobuf.ByteString$LiteralByteString")) {
				ByteString byteString = (ByteString) entry.getValue();
				fields.put(entry.getKey().getName(),
						new String(byteString.toByteArray()));
			} else {
				logger.error("不支持的类型：" + entry.getValue().getClass());
				throw new RuntimeException("protobuf decode error: "
						+ "不支持的类型：" + entry.getValue().getClass());
			}
		}

		Class<?> clazz = null;
		clazz = cache.get(className, new Callable<Class>() {
			public Class call() throws Exception {
				return Class.forName(className);
			}
		});
		Object instance = clazz.newInstance();
		Field[] fieldList = ReflectionUtils.getAllFields(clazz);
		for (Field f : fieldList) {
			ReflectionUtils.setFieldValue(instance, f, fields.get(f.getName()));
		}
		return instance;
	}

	/**
	 * 将对象实例转换成protobuf字节数据
	 * 
	 * 注意,对于pojo引用类型的属性,一定要定义在model包之下,否则不予处理.
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] encode(Object obj) throws Exception {
		return buildMessage(obj).toByteArray();
	}

	private Message buildMessage(Object obj) throws Exception {

		Class<? extends Object> clazz = obj.getClass();
		String className = clazz.getSimpleName();
		Descriptor desc = this.descriptors.get(className);
		Builder builder = DynamicMessage.newBuilder(desc);
		List<FieldDescriptor> fieldDescs = desc.getFields();
		Field[] fields = ReflectionUtils.getAllFields(clazz);
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		for (Field field : fields) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValueObject;
			fieldValueObject = field.get(obj);
			if (fieldValueObject != null) {
				fieldValues.put(fieldName, fieldValueObject);
			}
		}
		for (FieldDescriptor fieldDesc : fieldDescs) {
			String fieldName = fieldDesc.getName();
			Object val = fieldValues.get(fieldName);
			if (val != null) {
				if (fieldDesc.getType().getJavaType()
						.equals(JavaType.ENUM.BYTE_STRING)) {
					if (fieldDesc.isRepeated()) {
						Object[] params = (Object[]) val;
						for (int i = 0; i < params.length; i++) {
							Object paramVal = params[i];
							if (ReflectionUtils.isCustomizedModel(paramVal
									.getClass())) {
								builder.addRepeatedField(fieldDesc,
										buildMessage(paramVal).toByteArray());
							} else if (ReflectionUtils
									.isPrimitiveOrString(paramVal.getClass())) {
								builder.addRepeatedField(fieldDesc, String
										.valueOf(paramVal).getBytes());
							} else {// TODO nothing
								logger.error("不支持嵌套的bytes类型："
										+ paramVal.getClass());
								throw new RuntimeException("protobuf encode error: "
										+ "不支持的类型：" + paramVal.getClass());
							}
						}
					} else {
						if (ReflectionUtils.isCustomizedModel(val.getClass())) {
							builder.setField(fieldDesc, buildMessage(val)
									.toByteArray());
						} else if (ReflectionUtils.isPrimitiveOrString(val
								.getClass())) {
							builder.setField(fieldDesc, String.valueOf(val)
									.getBytes());
						} else if (val instanceof List) {
							builder.setField(fieldDesc,
									JacksonUtils.list2Json((List<?>) val)
											.getBytes());
						} else if (val instanceof Map) {
							builder.setField(fieldDesc,
									JacksonUtils.map2Json((Map) val).getBytes());
						} else if (val.getClass().isArray()) {
							builder.setField(fieldDesc,
									JacksonUtils.ary2Json((Object[]) val)
											.getBytes());
						} else {// TODO nothing
							logger.error("不支的bytes类型：" + val.getClass());
							throw new RuntimeException("protobuf encode error: "
									+ "不支持的类型：" + val.getClass());
						}
					}
				} else if (ReflectionUtils.isCustomizedModel(val.getClass())) {
					builder.setField(fieldDesc, buildMessage(val));
				} else if (ReflectionUtils.isPrimitiveOrString(val.getClass())) {
					builder.setField(fieldDesc, val);
				} else {// TODO nothing
					logger.error("不支持的类型："
							+ fieldDesc.getType().getJavaType());
					throw new RuntimeException("protobuf encode error: "
							+ "不支持的类型：" + fieldDesc.getType().getJavaType());
				}
			}
		}
		return builder.build();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.descFile.delete();
	}

	public static void main(String[] args) {
		ProtobufParserOrBuilder builder = new ProtobufParserOrBuilder(new File(
				"evarrpc.proto"));
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setSeq("seq");
		rpcRequest.setInterfaceName(HelloWorld.class.getName());
		rpcRequest.setMethod("sayHello");
		Object[] params = new Object[1];
		params[0] = "wtw";
		rpcRequest.setParams(params);
		byte[] encoded = null;
		try {
			encoded = builder.encode(rpcRequest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			rpcRequest = builder.decode(RpcRequest.class, encoded);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rpcRequest = builder.decode(RpcRequest.class, encoded);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
