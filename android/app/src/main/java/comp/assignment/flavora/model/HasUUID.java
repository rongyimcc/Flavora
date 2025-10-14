package comp.assignment.flavora.model;

/**
 * UUID标识符接口
 * <p>
 * 为具有UUID标识符的对象提供统一的访问接口。
 * 遵循参考应用的实体识别模式。
 *
 * @author Flavora Team
 */
public interface HasUUID {
    /**
     * 获取实体的UUID标识符
     *
     * @return UUID字符串
     */
    String getUUID();
}