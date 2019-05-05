
package org.sh.cryptonode.util

object CommonUtil {
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B =
  try { f(param) } finally { param.close() }
}
