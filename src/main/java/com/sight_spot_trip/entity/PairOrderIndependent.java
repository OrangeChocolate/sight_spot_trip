package com.sight_spot_trip.entity;

public class PairOrderIndependent<T extends Comparable> {
	private T element1;
	private T element2;

	@SuppressWarnings("unchecked")
	public PairOrderIndependent(T element1, T element2) {
		if (element1.compareTo(element2) < 0) {
			this.element1 = element1;
			this.element2 = element2;
		} else {
			this.element1 = element2;
			this.element2 = element1;
		}
	}

	public T getElement1() {
		return element1;
	}

	public void setElement1(T element1) {
		this.element1 = element1;
	}

	public T getElement2() {
		return element2;
	}

	public void setElement2(T element2) {
		this.element2 = element2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element1 == null) ? 0 : element1.hashCode());
		result = prime * result + ((element2 == null) ? 0 : element2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairOrderIndependent other = (PairOrderIndependent) obj;
		if (element1 == null) {
			if (other.element1 != null)
				return false;
		} else if (!element1.equals(other.element1))
			return false;
		if (element2 == null) {
			if (other.element2 != null)
				return false;
		} else if (!element2.equals(other.element2))
			return false;
		return true;
	}

}
