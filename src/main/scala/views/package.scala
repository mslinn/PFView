package views

class Memoize[T, R](f: T => R) {
  private[this] val vals = collection.mutable.Map.empty[T, R]

  def apply(x: T): R =
    if (vals.keySet.contains(x)) {
      vals(x)
    } else {
      val y = f(x)
      vals += x -> y
      y
    }
}

object Memoize {
  def apply[T, R](f: T => R) = new Memoize(f)
}
