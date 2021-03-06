package net.javachallenge.util.misc

// from http://d.hatena.ne.jp/gakuzo/20110701/1309522420

trait Closer[-A] {
  def close(value: A)
}

class Loan[A] private (value: A, closer: Closer[A]) {

  def foreach[B](f: A => B): B = try {
    f(value)
  } finally {
    closer.close(value)
  }

}

object Loan {

  def apply[A](value: A)(implicit closer: Closer[A]) = new Loan(value, closer)

  type Closeable = { def close() }
  implicit val closeable = new Closer[Closeable] {
    def close(value: Closeable) = value.close()
  }

  type Releasable = { def release() }
  implicit val destroyable = new Closer[Releasable] {
    def close(value: Releasable) = value.release()
  }

  type Disposable = { def dispose() }
  implicit val disposable = new Closer[Disposable] {
    def close(value: Disposable) = value.dispose()
  }

}