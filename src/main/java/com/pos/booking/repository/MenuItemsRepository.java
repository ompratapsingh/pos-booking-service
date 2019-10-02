package com.pos.booking.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.Items;
import com.pos.booking.domain.MenuItems;

@Repository
public class MenuItemsRepository {

	private static final String QUERY_FOR_FETCH_CATEGORY = "select Code, Descr, ColorCode from ArticleMaster";

	private static final String QUERY_FOR_ITEMS_LIST = "select txm.Name as tax_name ,txm.Scope as tax_scope "
			+ ",txm.Pct as tax_percentage ,txm.Addtax as add_tax "
			+ ",txm.Surcharge as surcharge,itm.Code as item_master_code, itm.Name as item_master_name, itm.Article,mm.Code,mm.Name,mm.Rate,mm.DiscPercent,mm.TaxCode,mm.AppGroup,mm.KotPrinter "
			+ "from ItemMaster itm inner join MenuMaster mm on itm.code = mm.ItemCode "
			+ "left join TaxMaster txm on mm.TaxCode=txm.Code inner join Stores on stores.code=(Select TOP(1) location from tablemaster Where Code=?) "
			+ "where stores.Menucode=mm.Code and txm.scope='S'" + "";

	private static final String QUERY_FOR_INSERT_CART_ITEMS = "insert into Kot (Branch,Prefix," + "Code," + "Qty,"
			+ "Rate," + "Disc, " + "DiscAmt," + "Docdate," + "Doctime, " + "TaxCode," + "NoofPrints," + "PaxNo,"
			+ "Remark," + "Store," + "TableCode," + "Type," + "Taxamt," + "AddtaxAmt," + "AddtaxAmt2, " + "Captain,"
			+ "HWSerial," + "Srl," + "Sno" + ") values (?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?,"
			+ "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?)";

	private static final String QUERY_FOR_INSERT_CART_DATA_IN_OPEN_TABLE = "insert into Opentables (Branch" + ",Type"
			+ ",Srl" + ",Prefix" + ",Captain" + ",Docdate" + ",PartyName" + ",px" + ",Tablecode" + ",Doctime"
			+ ",BillAmount" + ",PartyAddr" + ",PartyContact" + ",Store" + ",Noofprints" + ",PartyEmail" + ",Roundoff"
			+ ",DiscAmt" + ",HwSerial" + ",EnteredBy" + ") values (?," + "?," + "?," + "?," + "?," + "?," + "?," + "?,"
			+ "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?)";

	private static final String QUERY_FOR_FETCH_SRL = "Select isnull(Max(srl),0) as srl_num From KOT";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Category> fetchCategories() {

		return jdbcTemplate.query(QUERY_FOR_FETCH_CATEGORY,
				(ResultSet resultSet, int rowNumber) -> new Category(resultSet.getString("Code"),
						resultSet.getString("Descr"), resultSet.getString("ColorCode")));
	}

	public List<MenuItems> fetchMenuItems(String tableCode) {
		return jdbcTemplate.query(QUERY_FOR_ITEMS_LIST, new String[] { tableCode }, new RowMapper<MenuItems>() {

			@Override
			public MenuItems mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				MenuItems items = new MenuItems();
				items.setItem_master_code(resultSet.getString("item_master_code"));
				items.setItem_master_name(resultSet.getString("item_master_name"));
				items.setArticle(resultSet.getString("Article"));
				items.setCode(resultSet.getString("code"));
				items.setName(resultSet.getString("Name"));
				items.setRate(resultSet.getString("Rate"));
				items.setDiscPercent(resultSet.getString("DiscPercent"));
				items.setTaxCode(resultSet.getString("TaxCode"));
				items.setAppGroup(resultSet.getString("AppGroup"));
				items.setKotPrinter(resultSet.getString("KotPrinter"));
				items.setTax_name(resultSet.getString("tax_name"));
				items.setTax_scope(resultSet.getString("tax_scope"));
				items.setTax_percentage(resultSet.getString("tax_percentage"));
				items.setAdd_tax(resultSet.getString("tax_percentage"));
				items.setSurcharge(resultSet.getString("surcharge"));
				return items;
			}

		});

	}

	public boolean addToKot(CartItems cart) {
		int[] insertedRow = jdbcTemplate.batchUpdate(QUERY_FOR_INSERT_CART_ITEMS, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
				Items items = cart.getItems().get(index);
				preparedStatement.setString(1, cart.getBranch());
				preparedStatement.setString(2, cart.getPrefix());
				preparedStatement.setString(3, items.getCode());
				preparedStatement.setString(4, String.valueOf(items.getQty()));
				preparedStatement.setString(5, items.getRate());
				preparedStatement.setString(6, items.getDisc());
				preparedStatement.setString(7, items.getDiscAmt());
				preparedStatement.setString(8, cart.getDocdate());
				preparedStatement.setString(9, cart.getDoctime());
				preparedStatement.setString(10, cart.getTableCode());
				preparedStatement.setInt(11, cart.getNoofPrints());
				preparedStatement.setString(12, cart.getPaxNo());
				preparedStatement.setString(13, items.getRemarks());
				preparedStatement.setString(14, cart.getStoreCode());
				preparedStatement.setString(15, cart.getTableCode());
				preparedStatement.setString(16, cart.getType());
				preparedStatement.setString(17, items.getTaxamt());
				preparedStatement.setString(18, items.getAddtaxAmt());
				preparedStatement.setString(19, items.getAddtaxAmt2());
				preparedStatement.setString(20, cart.getCaptain());
				preparedStatement.setString(21, cart.getHwserial());
				preparedStatement.setString(22, cart.getSrl());
				preparedStatement.setString(23, items.getSno());
			}

			@Override
			public int getBatchSize() {
				return cart.getItems().size();
			}

		});
		return insertedRow.length == cart.getItems().size();
	}

	public boolean addToOpenTable(CartItems cart) {
		return jdbcTemplate.update(QUERY_FOR_INSERT_CART_DATA_IN_OPEN_TABLE, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, cart.getBranch());
				preparedStatement.setString(2, cart.getType());
				preparedStatement.setString(3, cart.getSrl());
				preparedStatement.setString(4, cart.getPrefix());
				preparedStatement.setString(5, cart.getCaptain());
				preparedStatement.setString(6, cart.getDocdate());
				preparedStatement.setString(7, cart.getPartyName());
				preparedStatement.setString(8, cart.getPaxNo());
				preparedStatement.setString(9, cart.getTableCode());
				preparedStatement.setString(10, cart.getDoctime());
				preparedStatement.setString(11, cart.getTotalbillAmount());
				preparedStatement.setString(12, cart.getPartyAddr());
				preparedStatement.setString(13, cart.getPartyContact());
				preparedStatement.setString(14, cart.getStoreCode());
				preparedStatement.setInt(15, cart.getNoofPrints());
				preparedStatement.setString(16, cart.getPartyEmail());
				preparedStatement.setString(17, cart.getRoundoff());
				preparedStatement.setString(18, cart.getTotalDiscAmt());
				preparedStatement.setString(19, cart.getHwserial());
				preparedStatement.setString(20, cart.getEnteredBy());
			}

		}) > 0;
	}

	public int getSrlNumber() {
		return Integer.valueOf(jdbcTemplate.queryForObject(QUERY_FOR_FETCH_SRL, String.class));
	}

}
