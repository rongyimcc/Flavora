package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.model.HasUUID;

import java.util.List;

/**
 * 数据访问对象（DAO）抽象基类
 * 提供所有数据访问操作的通用接口，抽象了底层数据源的实现细节（本项目使用Firebase）
 * 遵循reference-app的数据访问抽象模式
 *
 * @param <T> 此DAO管理的实体类型，必须实现HasUUID接口
 * @author Flavora Team
 * @version 1.0
 */
public abstract class DAO<T extends HasUUID> {

    /**
     * 添加实体到数据存储
     *
     * @param element  要添加的实体对象
     * @param listener 完成回调，用于处理添加操作的结果
     */
    public abstract void add(T element, OnCompleteListener<Void> listener);

    /**
     * 根据唯一标识符获取实体
     *
     * @param id       实体的唯一标识符
     * @param listener 完成回调，返回获取到的实体对象
     */
    public abstract void get(String id, OnCompleteListener<T> listener);

    /**
     * 获取此类型的所有实体
     *
     * @param listener 完成回调，返回所有实体的列表
     */
    public abstract void getAll(OnCompleteListener<List<T>> listener);

    /**
     * 根据唯一标识符删除实体
     *
     * @param id       要删除的实体唯一标识符
     * @param listener 完成回调，用于处理删除操作的结果
     */
    public abstract void delete(String id, OnCompleteListener<Void> listener);

    /**
     * 更新现有实体
     *
     * @param element  包含更新数据的实体对象
     * @param listener 完成回调，用于处理更新操作的结果
     */
    public abstract void update(T element, OnCompleteListener<Void> listener);
}
