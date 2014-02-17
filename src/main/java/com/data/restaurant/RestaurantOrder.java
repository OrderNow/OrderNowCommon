package com.data.restaurant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import com.database.DataConnection;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class RestaurantOrder {

	private static BiMap<Pair, String> tableRestOrderID_cache;

	private List<OrderedDish> dishes;
	private String orderId;
	private String customerId;
	private String restaurantId;
	private String tableId;
	private int tableNo;
	private int subOrderId;
	private String orderState;

	static {
		tableRestOrderID_cache = HashBiMap.create();
	}

	public int getTableNo() {
		return tableNo;
	}

	public void setTableNo(int tableNo) {
		this.tableNo = tableNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<OrderedDish> getDishes() {
		return dishes;
	}

	public void setDishes(List<OrderedDish> dishes) {
		this.dishes = dishes;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public int getSubOrderId() {
		return subOrderId;
	}

	public void setSubOrderId(int subOrderId) {
		this.subOrderId = subOrderId;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public static synchronized ArrayList<RestaurantOrder> getOrdersFronDB(
			String restaurantId, String state, ServletOutputStream debugger)
			throws IOException {
		ArrayList<RestaurantOrder> restOrder = DataConnection.getCurrentOrders(
				restaurantId, state, debugger);
		for (RestaurantOrder order : restOrder) {
			tableRestOrderID_cache.put(
					new Pair(order.getTableId(), order.getRestaurantId()),
					order.getOrderId());
		}
		return restOrder;
	}

	public static synchronized void completeOrder(String orderId) {
		BiMap<String, Pair> inverted = tableRestOrderID_cache.inverse();
		Pair table_rest = inverted.get(orderId);
		tableRestOrderID_cache.remove(table_rest);
		DataConnection.completeOrder(orderId);
	}

	public static String getOrderId(String tableId, String restaurantId) {
		Pair key = new Pair(tableId, restaurantId);
		if (tableRestOrderID_cache.containsKey(key)) {
			return tableRestOrderID_cache.get(key);
		}
		String orderId = DataConnection.getOrderId(tableId, restaurantId);
		if (orderId != null)
			tableRestOrderID_cache.put(key, orderId);
		return orderId;
	}
}

class Pair {
	private String tableId;
	private String restaurantId;

	public Pair(String tableId, String restaurantId) {
		this.tableId = tableId;
		this.restaurantId = restaurantId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}
}
