package com.biit.orbeon.form;

import java.util.List;

public interface IGroup extends ISubmittedObject {

	/**
	 * Is a loop.
	 * 
	 * @return
	 */
	public boolean isRepeatable();

	/**
	 * Mark this group as a loop.
	 * 
	 * @param repeatable
	 */
	public void setRepeatable(boolean repeatable);

	/**
	 * Returns the childs that are in an iteration. Returns a sublist of getChildren() with the elements in range
	 * between [iteration * childrenByIteration, iteration * childrenByIteration + childrenByIteration - 1]
	 * 
	 * @param iteration
	 * @return
	 */
	List<ISubmittedObject> getIteration(int iteration);

	/**
	 * Adds an iteration in the group.
	 */
	void increaseNumberOfIterations();
}
