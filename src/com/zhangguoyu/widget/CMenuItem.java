package com.zhangguoyu.widget;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface CMenuItem {
	
	/**
	 * ���ð�ťͼ��
	 * @param icon
	 * @return
	 */
	public CMenuItem setIcon(Drawable icon);
	
	/**
	 * ���ð�ťͼ��
	 * @param iconResId
	 * @return
	 */
	public CMenuItem setIcon(int iconResId);
	
	/**
	 * ���ð�ť����
	 * @param title
	 * @return
	 */
	public CMenuItem setTitle(CharSequence title);
	
	/**
	 * ���ñ���
	 * @param titleResId
	 * @return
	 */
	public CMenuItem setTitle(int titleResId);
	
	/**
	 * �����Զ���View
	 * @param customLayoutResId
	 * @return
	 */
	public CMenuItem setCustomView(int customLayoutResId);
	
	/**
	 * �����Զ���View
	 * @param customView
	 * @return
	 */
	public CMenuItem setCustomView(View customView);
	
	/**
	 * ���ð�ť�Ŀɼ���
	 * @param visibility
	 * @return
	 */
	public CMenuItem setVisibility(int visibility);
	
	/**
	 * ���ð�ť�Ƿ����
	 * @param enable ���true����ť���Ե������������¼������򣬲����Ե��
	 * @return
	 */
	public CMenuItem setEnabled(boolean enable);
	
	/**
	 * ���ð�ť�Ƿ�ѡ��
	 * @param selected ���ture����ť����Ϊѡ��״̬���������ť��ѡ��״̬�����򣬰�ť����Ϊ��ѡ��״̬
	 * @return
	 */
	public CMenuItem setSelected(boolean selected);
	
	/**
	 * ���ð�ť��intent
	 * �����������Ч��intent������ð�ťʱ�����Զ����ָ����intent����Activity
	 * @param intent
	 * @return
	 */
	public CMenuItem setIntent(Intent intent);
	
	/**
	 * ���ð�ť��id
	 * @param id
	 * @return
	 */
	public CMenuItem setItemId(int id);
	
	/**
	 * ���÷���id
	 * @param groupId
	 * @return
	 */
	public CMenuItem setGroupId(int groupId);
	
	/**
	 * �ж��Ƿ����Ӱ�ť
	 * @return
	 */
	public boolean hasSubMenu();
	
	/**
	 * ��ȡ�Ӱ�ť
	 * @return
	 */
	public CSubMenu getSubMenu();
	
	/**
	 * ��ȡͼ��
	 * @return
	 */
	public Drawable getIcon();
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public CharSequence getTitle();
	
	/**
	 * ��ȡ�Զ���View
	 * @return
	 */
	public View getCustomView();
	
	/**
	 * ��ȡIntent
	 * @return
	 */
	public Intent getIntent();
	
	/**
	 * ��ȡ��ť��Ŀɼ���
	 * @return
	 */
	public int getVisibility();
	
	/**
	 * �ж��Ƿ�ɵ��
	 * @return
	 */
	public boolean isEnabled();
	
	/**
	 * �ж��Ƿ�Ϊѡ��״̬
	 * @return
	 */
	public boolean isSelected();
	
	/**
	 * ��ȡ����id
	 * @return
	 */
	public int getGroupId();
	
	/**
	 * ��ȡid
	 * @return
	 */
	public int getItemId();

}
