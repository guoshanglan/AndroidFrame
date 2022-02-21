package androidx.fragment.app.result

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlin.properties.Delegates

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 15:55
 *    desc   : fragment消息
 */
class FragmentResult private constructor() : Parcelable {

    lateinit var requestKey: String
    var resultCode by Delegates.notNull<Int>()
    var data: Bundle? = null

    constructor(requestKey: String, resultCode: Int, data: Bundle?) : this() {
        this.requestKey = requestKey
        this.resultCode = resultCode
        this.data = data
    }

    constructor(parcel: Parcel) : this() {
        this.requestKey = parcel.readString()!!
        this.resultCode = parcel.readInt()
        this.data = if (parcel.readInt() == 0) null else Bundle.CREATOR.createFromParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(requestKey)
        parcel.writeInt(resultCode)
        data?.let {
            parcel.writeInt(0)
            it.writeToParcel(parcel, flags)
        } ?: kotlin.run {
            parcel.writeInt(1)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FragmentResult> {
        override fun createFromParcel(parcel: Parcel): FragmentResult {
            return FragmentResult(parcel)
        }

        override fun newArray(size: Int): Array<FragmentResult?> {
            return arrayOfNulls(size)
        }
    }


}