package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

/**
 * Vista de {@code subList} compartida por {@link MyArrayList} y {@link MyLinkedList}.
 *
 * <p>No copia nada: guarda la lista raíz, el desplazamiento absoluto y el tamaño de la vista, y
 * delega en la raíz. Así el anidamiento ({@code lista.subList(...).subList(...)}) funciona sin
 * casos especiales.</p>
 *
 * <p>Semántica, igual que en el JDK:</p>
 * <ul>
 *   <li>Las mutaciones hechas <em>a través</em> de la vista se reflejan en la raíz.</li>
 *   <li>Una modificación estructural de la raíz <em>desde fuera</em> deja la vista obsoleta y la
 *       siguiente operación lanza {@link ConcurrentModificationException}.</li>
 * </ul>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * MyArrayList<String> lista = new MyArrayList<>();
 * lista.addAll(List.of("a", "b", "c", "d"));
 *
 * List<String> vista = lista.subList(1, 3);   // ["b", "c"]  (no es una copia)
 *
 * vista.set(0, "z");
 * System.out.println(lista);      // [a, z, c, d]   ← el cambio se ve en el padre
 *
 * vista.add("y");
 * System.out.println(lista);      // [a, z, y, c, d] ← insertado en la posición 2
 *
 * lista.add("e");                 // modificación externa: la vista queda obsoleta
 * vista.get(0);                   // lanza ConcurrentModificationException
 * }</pre>
 *
 * @param <E> tipo de los elementos
 */
final class MySubList<E> extends AbstractMyList<E> {

	private final AbstractMyList<E> root;

	/** Desplazamiento absoluto del primer elemento de la vista dentro de la raíz. */
	private final int offset;

	/** Tamaño actual de la vista. */
	private int size;

	/** Valor esperado del {@code modCount} de la raíz. */
	private int expectedModCount;

	MySubList(AbstractMyList<E> root, int offset, int size) {
		this.root = root;
		this.offset = offset;
		this.size = size;
		this.expectedModCount = root.modCount();
	}

	@Override
	public int size() {
		checkForComodification();
		return size;
	}

	@Override
	public E get(int index) {
		checkForComodification();
		checkIndex(index);
		return root.get(offset + index);
	}

	@Override
	public E set(int index, E element) {
		checkForComodification();
		checkIndex(index);
		return root.set(offset + index, element);
	}

	@Override
	public void add(int index, E element) {
		checkForComodification();
		checkPositionIndex(index);
		root.add(offset + index, element);
		size++;
		expectedModCount = root.modCount();
	}

	@Override
	public E remove(int index) {
		checkForComodification();
		checkIndex(index);
		E removed = root.remove(offset + index);
		size--;
		expectedModCount = root.modCount();
		return removed;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkForComodification();
		checkPositionIndex(index);
		return new MyListIterator<>(this, index);
	}

	/**
	 * El contador de modificaciones que vigila el iterador es el de la raíz: cualquier cambio
	 * estructural, venga de donde venga, debe detectarse.
	 */
	@Override
	int modCount() {
		return root.modCount();
	}

	@Override
	int absoluteOffset() {
		return offset;
	}

	@Override
	AbstractMyList<E> root() {
		return root;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		checkSubListRange(fromIndex, toIndex);
		return new MySubList<>(root, offset + fromIndex, toIndex - fromIndex);
	}

	private void checkForComodification() {
		if (root.modCount() != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}
}
