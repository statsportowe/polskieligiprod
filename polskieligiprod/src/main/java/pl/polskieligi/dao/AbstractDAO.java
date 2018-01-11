package pl.polskieligi.dao;

public interface AbstractDAO<T> {
	public T retrieveById(Long id);
	public T saveOrUpdate(T obj);		
}
