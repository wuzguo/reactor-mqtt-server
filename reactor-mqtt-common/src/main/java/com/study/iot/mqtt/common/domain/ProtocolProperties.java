package com.study.iot.mqtt.common.domain;

import com.study.iot.mqtt.common.annocation.ProtocolType;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 10:30
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolProperties {

    /**
     * 端口
     */
    private Integer port;

    /**
     * 协议
     */
    private ProtocolType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolProperties that = (ProtocolProperties) o;
        return Objects.equals(port, that.port) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, type);
    }
}
