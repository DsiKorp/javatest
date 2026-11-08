package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Base común de {@link MyArrayList} y {@link MyLinkedList}.
 *
 * <p>Reproduce el reparto del JDK entre {@code AbstractList} y sus subclases: aquí viven todos
 * los algoritmos que sólo dependen de {@code get}/{@code set}/{@code add}/{@code remove}, y cada
 * subclase implementa las seis primitivas restantes. Así {@code equals}, {@code hashCode},
 * {@code indexOf}, {@code toArray}, {@code toString}, {@code addAll}, {@code removeAll},
 * {@code retainAll} o {@code sort} están escritos <strong>una sola vez</strong> y valen para las dos
 * estructuras.</p>
 *
 * <p>Dos detalles que replican el JDK y que conviene destacar:</p>
 * <ul>
 *   <li>{@link #equals(Object)} compara <em>elemento a elemento contra cualquier</em>
 *       {@link List}, de modo que una {@code MyArrayList} y una {@code MyLinkedList} con los
 *       mismos elementos son iguales entre sí, igual que {@code ArrayList.equals(LinkedList)}.</li>
 *   <li>{@link #sort(Comparator)} está sobreescrito con un <em>merge sort propio</em>: el
 *       método {@code default} de {@code List} delega en {@code Arrays.sort}, que está prohibido
 *       en este paquete.</li>
 * </ul>
 *
 * <p><strong>Ejemplo: así se implementa una lista propia.</strong> Basta con las seis
 * primitivas; todo lo demás (los {@code indexOf}, los {@code addAll}, el {@code toArray}, el
 * {@code equals}…) sale gratis de esta clase:</p>
 * <pre>{@code
 * final class MiListaDeEnteros extends AbstractMyList<Integer> {
 *
 *     private final int[] datos = new int[10];
 *     private int size;
 *
 *     @Override public int size() { return size; }
 *
 *     @Override public Integer get(int index) {
 *         if (index < 0 || index >= size) {
 *             throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
 *         }
 *         return datos[index];
 *     }
 *
 *     @Override public Integer set(int index, Integer element) {
 *         Integer anterior = get(index);
 *         datos[index] = element;
 *         return anterior;
 *     }
 *
 *     @Override public void add(int index, Integer element) {
 *         for (int i = size; i > index; i--) {
 *             datos[i] = datos[i - 1];
 *         }
 *         datos[index] = element;
 *         size++;
 *     }
 *
 *     @Override public Integer remove(int index) {
 *         Integer eliminado = get(index);
 *         for (int i = index; i < size - 1; i++) {
 *             datos[i] = datos[i + 1];
 *         }
 *         size--;
 *         return eliminado;
 *     }
 *
 *     @Override public ListIterator<Integer> listIterator(int index) {
 *         return new MiIterador(this, index);   // un iterador propio, fail-fast
 *     }
 * }
 * }</pre>
 *
 * <p>Nótese que {@code indexOf}, {@code contains}, {@code remove(Object)}, {@code addAll},
 * {@code removeAll}, {@code retainAll}, {@code toArray}, {@code toString}, {@code equals},
 * {@code hashCode} y {@code sort} no aparecen en la subclase: se resuelven en términos de
 * {@code get}/{@code set}/{@code add}/{@code remove}, que es justo lo que se busca al separar
 * los algoritmos de la estructura.</p>
 *
 * @param <E> tipo de los elementos
 */
public abstract class AbstractMyList<E> implements MyList<E> {

	/**
	 * Contador de modificaciones estructurales. Lo usa el iterador para detectar modificaciones
	 * concurrentes (comportamiento fail-fast).
	 */
	protected int modCount;

	/**
	 * Constructor protegido: sólo las subclases de este paquete pueden instanciar la clase base.
	 */
	protected AbstractMyList() {
	}

	/**
	 * @return el contador de modificaciones estructurales actual
	 */
	int modCount() {
		return modCount;
	}

	// ------------------------------------------------------------------
	// Primitivas que debe aportar cada implementación
	// ------------------------------------------------------------------

	/**
	 * @return el número de elementos de la lista
	 */
	public abstract int size();

	/**
	 * @param index posición del elemento
	 * @return el elemento en esa posición
	 * @throws IndexOutOfBoundsException si la posición no es válida
	 */
	public abstract E get(int index);

	/**
	 * @param index posición del elemento
	 * @param element nuevo valor
	 * @return el valor anterior
	 * @throws IndexOutOfBoundsException si la posición no es válida
	 */
	public abstract E set(int index, E element);

	/**
	 * @param index posición de inserción
	 * @param element elemento a insertar
	 * @throws IndexOutOfBoundsException si la posición no es válida
	 */
	public abstract void add(int index, E element);

	/**
	 * @param index posición del elemento
	 * @return el elemento eliminado
	 * @throws IndexOutOfBoundsException si la posición no es válida
	 */
	public abstract E remove(int index);

	/**
	 * @param index posición inicial del iterador
	 * @return un iterador bidireccional situado en {@code index}
	 */
	public abstract ListIterator<E> listIterator(int index);

	// ------------------------------------------------------------------
	// Implementaciones concretas comunes
	// ------------------------------------------------------------------

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean add(E element) {
		add(size(), element);
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public void clear() {
		for (int i = size() - 1; i >= 0; i--) {
			remove(i);
		}
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		checkSubListRange(fromIndex, toIndex);
		return new MySubList<>(root(), absoluteOffset() + fromIndex, toIndex - fromIndex);
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < size(); i++) {
			if (Objects.equals(o, get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size() - 1; i >= 0; i--) {
			if (Objects.equals(o, get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object element : c) {
			if (!contains(element)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index < 0) {
			return false;
		}
		remove(index);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E element : c) {
			add(element);
			modified = true;
		}
		return modified;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		int position = index;
		for (E element : c) {
			add(position++, element);
		}
		return !c.isEmpty();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Iterator<E> it = iterator(); it.hasNext();) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		for (Iterator<E> it = iterator(); it.hasNext();) {
			if (!c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		for (int i = 0; i < size(); i++) {
			action.accept(get(i));
		}
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		boolean removed = false;
		for (Iterator<E> it = iterator(); it.hasNext();) {
			if (filter.test(it.next())) {
				it.remove();
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		for (int i = 0; i < size(); i++) {
			set(i, operator.apply(get(i)));
		}
	}

	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, Spliterator.ORDERED);
	}

	// ------------------------------------------------------------------
	// equals / hashCode / toString / toArray
	// ------------------------------------------------------------------

	/**
	 * Compara elemento a elemento contra cualquier {@link List}, no sólo contra otra
	 * {@code MyList}. Así {@code MyArrayList} y {@code MyLinkedList} con los mismos elementos
	 * resultan iguales, como en el JDK.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof List<?> other)) {
			return false;
		}
		if (other.size() != size()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!Objects.equals(get(i), other.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * El algoritmo 31-based del JDK, de modo que dos listas iguales (aunque de implementaciones
	 * distintas) producen el mismo hash.
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < size(); i++) {
			hash = 31 * hash + Objects.hashCode(get(i));
		}
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(get(i));
		}
		return sb.append(']').toString();
	}

	@Override
	public Object[] toArray() {
		Object[] result = new Object[size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = get(i);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int size = size();
		if (a.length < size) {
			// Mismo comportamiento que el JDK: se crea un array del mismo tipo de componente.
			T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
			for (int i = 0; i < size; i++) {
				result[i] = (T) get(i);
			}
			return result;
		}
		for (int i = 0; i < size; i++) {
			a[i] = (T) get(i);
		}
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	// ------------------------------------------------------------------
	// sort: merge sort propio (Arrays.sort está prohibido en este paquete)
	// ------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	@Override
	public void sort(Comparator<? super E> comparator) {
		if (comparator == null) {
			throw new NullPointerException("comparator");
		}
		int size = size();
		if (size < 2) {
			return;
		}
		Object[] elements = toArray();
		Object[] buffer = new Object[size];
		mergeSort(elements, buffer, 0, size, comparator);
		for (int i = 0; i < size; i++) {
			set(i, (E) elements[i]);
		}
	}

	private static void mergeSort(Object[] source, Object[] buffer, int low, int high,
			Comparator<?> comparator) {
		if (high - low < 2) {
			return;
		}
		int mid = (low + high) >>> 1;
		mergeSort(source, buffer, low, mid, comparator);
		mergeSort(source, buffer, mid, high, comparator);
		merge(source, buffer, low, mid, high, comparator);
	}

	@SuppressWarnings("unchecked")
	private static void merge(Object[] source, Object[] buffer, int low, int mid, int high,
			Comparator<?> comparator) {
		Comparator<Object> cmp = (Comparator<Object>) comparator;
		int left = low;
		int right = mid;
		int target = low;
		while (left < mid && right < high) {
			// "< 0" mantiene la estabilidad del merge sort.
			if (cmp.compare(source[right], source[left]) < 0) {
				buffer[target++] = source[right++];
			}
			else {
				buffer[target++] = source[left++];
			}
		}
		while (left < mid) {
			buffer[target++] = source[left++];
		}
		while (right < high) {
			buffer[target++] = source[right++];
		}
		for (int i = low; i < high; i++) {
			source[i] = buffer[i];
		}
	}

	// ------------------------------------------------------------------
	// Utilidades internas
	// ------------------------------------------------------------------

	/**
	 * Valida una posición de lectura o de escritura de un elemento.
	 *
	 * @param index posición a validar
	 * @throws IndexOutOfBoundsException si {@code index} no está en {@code [0, size)}
	 */
	protected void checkIndex(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + size());
		}
	}

	/**
	 * Valida una posición de inserción, que puede ser justo una más allá del último elemento.
	 *
	 * @param index posición a validar
	 * @throws IndexOutOfBoundsException si {@code index} no está en {@code [0, size]}
	 */
	protected void checkPositionIndex(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + size());
		}
	}

	/**
	 * Valida el rango de una vista de {@code subList}.
	 *
	 * @param fromIndex posición inicial, inclusiva
	 * @param toIndex posición final, exclusiva
	 * @throws IndexOutOfBoundsException si el rango no es válido para el tamaño actual
	 */
	protected void checkSubListRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException(
					"Sublist range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + size());
		}
	}

	/**
	 * Desplazamiento absoluto de esta lista respecto de la lista raíz. Una vista de
	 * {@code subList} devuelve el suyo para poder anidar subList sin casos especiales.
	 *
	 * @return desplazamiento respecto de la raíz
	 */
	int absoluteOffset() {
		return 0;
	}

	/**
	 * @return la lista raíz (esta misma, salvo en las vistas de {@code subList})
	 */
	AbstractMyList<E> root() {
		return this;
	}
}
