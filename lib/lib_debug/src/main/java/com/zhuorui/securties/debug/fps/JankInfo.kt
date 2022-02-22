package com.zhuorui.securties.debug.fps

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


/**
 * JankInfo
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  13:34
 */
@Entity
open class JankInfo {
    // 本地数据库中的id，id必须使用使用注解 @Id，表示自增长，如需手动管理应该用 @Id(assignable = true)
    @Id
    var dbId: Long? = null

    var occurredTime: Long? = null
    var frameCost: Int? = null
    var resolved: Boolean = false
    @Convert(dbType = String::class, converter = JsonConverter::class)
    var stackCountEntries: List<Pair<String, Int>>? = null
}