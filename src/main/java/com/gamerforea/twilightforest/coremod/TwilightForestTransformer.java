package com.gamerforea.twilightforest.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class TwilightForestTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.entity.EntityLivingBase"))
		{
			CoreMod.LOGGER.info("Transforming {}...", transformedName);
			byte[] bytes = transformEntityLivingBase(basicClass);
			CoreMod.LOGGER.info("{} transformed", transformedName);
			return bytes;
		}
		return basicClass;
	}

	public static byte[] transformEntityLivingBase(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		new ClassReader(basicClass).accept(classNode, 0);

		MethodNode methodNode = findMethod(classNode, "<init>", "(Lnet/minecraft/world/World;)V");
		String aeaMethodOwner = "net/minecraft/entity/EntityLivingBase";
		String aeaMethodName = CoreMod.isObfuscated() ? "func_110147_ax" : "applyEntityAttributes";
		String aeaMethodDesc = "()V";

		List<MethodInsnNode> aeaMethodInsns = new ArrayList<>(1);

		for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); )
		{
			AbstractInsnNode insnNode = iterator.next();
			if (insnNode.getOpcode() == Opcodes.INVOKEVIRTUAL)
			{
				MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
				if (methodInsnNode.owner.equals(aeaMethodOwner) && methodInsnNode.name.equals(aeaMethodName) && methodInsnNode.desc.equals(aeaMethodDesc))
					aeaMethodInsns.add(methodInsnNode);
			}
		}

		if (aeaMethodInsns.isEmpty())
			CoreMod.LOGGER.warn("{}.{}{} method calls not found in {}.{}{}", aeaMethodOwner, aeaMethodName, aeaMethodDesc, classNode.name, methodNode.name, methodNode.desc);
		else
		{
			for (MethodInsnNode methodInsnNode : aeaMethodInsns)
			{
				InsnList hookList = new InsnList();
				hookList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				hookList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/gamerforea/twilightforest/balance/BalanceConfig", "applyTo", "(Lnet/minecraft/entity/EntityLivingBase;)V", false));
				methodNode.instructions.insert(methodInsnNode, hookList);
			}

			CoreMod.LOGGER.warn("{} {}.{}{} method calls hooked in {}.{}{}", aeaMethodInsns.size(), aeaMethodOwner, aeaMethodName, aeaMethodDesc, classNode.name, methodNode.name, methodNode.desc);
		}

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static MethodNode findMethod(ClassNode classNode, String name, String desc)
	{
		for (MethodNode methodNode : classNode.methods)
		{
			if (methodNode.name.equals(name) && methodNode.desc.equals(desc))
				return methodNode;
		}
		throw new NullPointerException("Method " + classNode.name + '/' + name + desc + " not found");
	}
}
