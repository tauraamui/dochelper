package co.uk.taurasystems.application.utils

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.lang.reflect.Field
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by kongsin on 26/4/2559.
 */
class XMLParser {

    fun fromXML(url : URL, obj : Any) : Any {
        val scanner = Scanner(url.openStream())
        val strBuilder = StringBuilder()
        while (scanner.hasNext()){
            strBuilder.append(scanner.nextLine())
        }
        return fromXML(strBuilder.toString(), obj)
    }

    fun fromXML(xml: String, obj: Any): Any {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val strBuilder = StringBuilder()
        strBuilder.append(xml)
        val byteStream = ByteArrayInputStream(strBuilder.toString().toByteArray())
        val doc = builder.parse(byteStream)
        doc.documentElement.normalize()
        getNodeObject(doc.documentElement, obj)
        return obj
    }

    private fun getNodeObject(element: Element, _obj: Any): Any {
        val fields = _obj.javaClass.declaredFields
        for (f in fields) {
            if (isNativeObject(f)) {
                putValue(f, _obj, element)
            } else {
                if (f.type.isArray) {
                    val elm = getData(element.getElementsByTagName(f.name))
                    if (elm.size > 0) {
                        val tmpObject = java.lang.reflect.Array.newInstance(f.type.componentType, elm.size) as Array<Any>
                        for (i in 0..tmpObject.size - 1) {
                            tmpObject[i] = getNodeObject(elm[i] as Element, f.type.componentType.newInstance())
                        }
                        f.set(_obj, tmpObject)
                    }
                } else {
                    val tmpObject = getNodeObject(element, f.type.newInstance())
                    f.set(_obj, tmpObject)
                }
            }
        }
        return _obj
    }

    private fun getData(list: NodeList): Array<Element?> {
        val e: Array<Element?> = arrayOfNulls(list.length)
        if (e.size == 0) return e
        for (i in 0..list.length - 1) {
            e[i] = list.item(i) as Element?
        }
        return e
    }

    private fun putValue(f: Field, obj: Any, value: Element): Any {
        val type = f.type.canonicalName
        val v = getData(value.getElementsByTagName(f.name))
        if (type.startsWith(Char :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Char?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toCharArray()[i]
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setChar(obj, e!!.textContent[0])
                }
            }
        } else if (type.startsWith(Int :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Int?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toInt()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setInt(obj, e!!.textContent.trim().toInt())
                }
            }
        } else if (type.startsWith(Short :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Short?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toShort()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setShort(obj, e!!.textContent.toShort())
                }
            }
        } else if (type.startsWith(Long :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Long?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toLong()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setLong(obj, e!!.textContent.toLong())
                }
            }
        } else if (type.startsWith(Boolean :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Boolean?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toBoolean()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setBoolean(obj, e!!.textContent.toBoolean())
                }
            }
        } else if (type.startsWith(Float :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Float?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toFloat()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setFloat(obj, e!!.textContent.toFloat())
                }
            }
        } else if (type.startsWith(Double :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<Double?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent.toDouble()
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.setDouble(obj, e!!.textContent.toDouble())
                }
            }
        } else if (type.startsWith(String :: class.java.name)) {
            if (f.type.isArray) {
                val ch = arrayOfNulls<String?>(v.size)
                for (i in 0..ch.size - 1) {
                    ch[i] = v[i]!!.textContent
                }
                f.set(obj, ch)
            } else {
                for (e in v) {
                    f.set(obj, e!!.textContent)
                }
            }
        }
        return obj
    }

    private fun isNativeObject(f: Field): Boolean {
        val type = f.type.canonicalName
        if (type.startsWith(Char :: class.java.name)) {
            return true
        } else if (type.startsWith(Int :: class.java.name)) {
            return true
        } else if (type.startsWith(Short :: class.java.name)) {
            return true
        } else if (type.startsWith(Long :: class.java.name)) {
            return true
        } else if (type.startsWith(Float :: class.java.name)) {
            return true
        } else if (type.startsWith(Double :: class.java.name)) {
            return true
        } else if (type.startsWith(Boolean  :: class.java.name)) {
            return true
        } else {
            return type.startsWith(String :: class.java.name)
        }
    }

    fun toXML(obj: Any): String {
        val fields = obj.javaClass.declaredFields
        val builder = StringBuilder()
        builder.append("\n")
        builder.append(openTag(obj.javaClass.simpleName))
        builder.append("\n")
        for (_f in fields) {
            if (isNativeObject(_f)) {
                if (_f.type.isArray) {
                    val data = _f.get(obj) as Array<*>
                    for (o in data) {
                        builder.append("\n")
                        builder.append(openTag(_f.name))
                        builder.append(o.toString())
                        builder.append(closeTag(_f.name))
                        builder.append("\n")
                    }
                } else {
                    builder.append("\n")
                    builder.append(openTag(_f.name))
                    builder.append(_f.get(obj).toString())
                    builder.append(closeTag(_f.name))
                    builder.append("\n")
                }
            } else {
                if (_f.type.isArray) {
                    val data = _f.get(obj) as Array<*>
                    for (o in data) {
                        builder.append(toXML(o as Any))
                    }
                } else {
                    val tmpObject = _f.get(obj)
                    builder.append(toXML(tmpObject))
                }
            }
        }
        builder.append("\n")
        builder.append(closeTag(obj.javaClass.simpleName))
        return builder.toString().replace("\n\n", "\n")
    }

    private fun openTag(tagName: String): String {
        val b = StringBuilder()
        b.append("<")
        b.append(tagName)
        b.append(">")
        return b.toString()
    }

    private fun closeTag(tagName: String): String {
        val b = StringBuilder()
        b.append("</")
        b.append(tagName)
        b.append(">")
        return b.toString()
    }

}