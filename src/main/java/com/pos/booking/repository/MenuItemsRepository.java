package com.pos.booking.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.Items;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.domain.PaymentDetails;

@Repository
public class MenuItemsRepository {

	private static final String QUERY_FOR_FETCH_CATEGORY = "select Code, Descr, ColorCode from ArticleMaster";

	private org.slf4j.Logger log = LoggerFactory.getLogger(MenuItemsRepository.class);

	private static final String QUERY_FOR_ITEMS_LIST = "select txm.Name as tax_name ,txm.Scope as tax_scope,stores.code as store_code "
			+ ",txm.Pct as tax_percentage ,txm.Addtax as add_tax "
			+ ",txm.Surcharge as surcharge,itm.Code as item_master_code, itm.Name as item_master_name, itm.Article,mm.Code,mm.Name,mm.Rate,mm.DiscPercent,mm.TaxCode,mm.AppGroup,mm.KotPrinter "
			+ "from ItemMaster itm inner join MenuMaster mm on itm.code = mm.ItemCode "
			+ "left join TaxMaster txm on mm.TaxCode=txm.Code inner join Stores on stores.code=(Select TOP(1) location from tablemaster Where Code=?) "
			+ "where stores.Menucode=mm.Code and txm.scope='S'" + "";

	private static final String QUERY_FOR_INSERT_CART_ITEMS = "insert into Kot (Branch,Prefix," + "Code," + "Qty,"
			+ "Rate," + "Disc, " + "DiscAmt," + "Doctime, " + "TaxCode," + "NoofPrints," + "PaxNo," + "Remark,"
			+ "Store," + "TableCode," + "Type," + "Taxamt," + "AddtaxAmt," + "AddtaxAmt2, " + "Captain," + "HWSerial,"
			+ "Srl," + "Sno" + ") values (?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?,"
			+ "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?)";

	private static final String QUERY_FOR_INSERT_CART_DATA_IN_OPEN_TABLE = "insert into Opentables (Branch" + ",Type"
			+ ",Srl" + ",Prefix" + ",Captain" + ",Docdate" + ",PartyName" + ",px" + ",Tablecode" + ",Doctime"
			+ ",BillAmount" + ",PartyAddr" + ",PartyContact" + ",Store" + ",Noofprints" + ",PartyEmail" + ",Roundoff"
			+ ",DiscAmt" + ",HwSerial" + ",EnteredBy" + ") values (?," + "?," + "?," + "?," + "?," + "?," + "?," + "?,"
			+ "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?)";

	/*
	 * private static final String QUERY_FOR_FETCH_KOT_DETAILS_BY_TABLE_CODE =
	 * "SELECT " + "otb.Branch," + "otb.Type," + "otb.Srl," + "otb.Prefix," +
	 * "otb.Captain," + "otb.Docdate," + "otb.PartyName," + "otb.px," +
	 * "otb.Tablecode," + "otb.Doctime," + "otb.BillAmount," + "otb.PartyAddr," +
	 * "otb.PartyContact," + "otb.Store," + "otb.Noofprints," + "otb.PartyEmail," +
	 * "otb.Roundoff," + "otb.DiscAmt," + "otb.HwSerial," + "otb.EnteredBy," +
	 * "kt.SNO as kt_sno," + "kt.Code as kt_Code," + "kt.Qty as kt_Qty," +
	 * "kt.Rate as kt_Rate," + "kt.Disc as kt_Disc," + "kt.DiscAmt as kt_DiscAmt," +
	 * "kt.Docdate as kt_Docdate," + "kt.Doctime kt_Doctime," +
	 * "kt.TaxCode as kt_TaxCode," + "kt.Remark as kt_Remark," +
	 * "kt.Taxamt as kt_Taxamt," + "kt.AddtaxAmt as kt_AddtaxAmt" +
	 * "from Opentables otb inner join KOT kt on otb.Srl=kt.srl where otb.Tablecode=?"
	 * ;
	 * 
	 */

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
				items.setStore_code(resultSet.getString("store_code"));
				return items;
			}

		});

	}

	public boolean addToKot(CartItems cart) {
		int[] insertedRow = null;
		try {
			insertedRow = jdbcTemplate.batchUpdate(QUERY_FOR_INSERT_CART_ITEMS, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
					int cIndex = 0;
					Items items = cart.getItems().get(index);
					preparedStatement.setString(++cIndex, cart.getBranch());
					preparedStatement.setString(++cIndex, cart.getPrefix());
					preparedStatement.setString(++cIndex, items.getCode());
					preparedStatement.setString(++cIndex, String.valueOf(items.getQty()));
					preparedStatement.setString(++cIndex, items.getRate());
					preparedStatement.setString(++cIndex, items.getDisc());
					preparedStatement.setString(++cIndex, items.getDiscAmt());
					// preparedStatement.setDate(++cIndex, new
					// java.sql.Date(System.currentTimeMillis()));;
					// preparedStatement.setDate(++cIndex,new
					// Date(LocalDate.now().getLong(ChronoField.EPOCH_DAY)));
					preparedStatement.setString(++cIndex, cart.getDoctime());
					preparedStatement.setString(++cIndex, cart.getTableCode());
					preparedStatement.setInt(++cIndex, cart.getNoofPrints());
					preparedStatement.setString(++cIndex, cart.getPaxNo());
					preparedStatement.setString(++cIndex, items.getRemarks());
					preparedStatement.setString(++cIndex, cart.getStoreCode());
					preparedStatement.setString(++cIndex, cart.getTableCode());
					preparedStatement.setString(++cIndex, cart.getType());
					preparedStatement.setString(++cIndex, items.getTaxamt());
					preparedStatement.setString(++cIndex, items.getAddtaxAmt());
					preparedStatement.setString(++cIndex, items.getAddtaxAmt2());
					preparedStatement.setString(++cIndex, cart.getCaptain());
					preparedStatement.setString(++cIndex, cart.getHwserial());
					preparedStatement.setString(++cIndex, cart.getSrl());
					preparedStatement.setString(++cIndex, items.getSno());
				}

				@Override
				public int getBatchSize() {
					return cart.getItems().size();
				}

			});

		} catch (Exception sqlException) {
			log.error("Error while adding cart details in database", sqlException);
		}

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

				preparedStatement.setDate(6, new Date(LocalDate.now().getLong(ChronoField.EPOCH_DAY)));

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

	public CartItems getKotDetailsById(String tableId) {
		StringBuilder QUERY_FOR_FETCH_KOT_DETAILS_BY_TABLE_CODE = new StringBuilder();
		QUERY_FOR_FETCH_KOT_DETAILS_BY_TABLE_CODE
				.append("SELECT   otb.Branch,  otb.Type,"
						+ "otb.Srl,  otb.Prefix,  otb.Captain,  otb.Docdate,  otb.PartyName,  otb.px,"
						+ "otb.Tablecode,  otb.Doctime,  otb.BillAmount,  otb.PartyAddr,  otb.PartyContact,"
						+ "otb.Store,  otb.Noofprints,  otb.PartyEmail,  otb.Roundoff,  otb.DiscAmt,  otb.HwSerial,"
						+ "otb.EnteredBy,  kt.SNO as kt_sno,  kt.Code as kt_Code,  kt.Qty as kt_Qty,")
				.append("kt.Rate as kt_Rate,  kt.Disc as kt_Disc,  kt.DiscAmt as kt_DiscAmt,  kt.Docdate as kt_Docdate,"
						+ "kt.Doctime kt_Doctime,  kt.TaxCode as kt_TaxCode,  kt.Remark as kt_Remark,"
						+ "kt.Taxamt as kt_Taxamt,  kt.AddtaxAmt as kt_AddtaxAmt,kt.AddtaxAmt2 kt_AddtaxAmt2,itmst.Name as item_name")
				.append(" from Opentables otb inner join KOT kt on otb.Srl=kt.srl inner join ItemMaster as itmst on itmst.Code=kt.Code where otb.Tablecode=?");

		return jdbcTemplate.query(QUERY_FOR_FETCH_KOT_DETAILS_BY_TABLE_CODE.toString(),
				new ResultSetExtractor<CartItems>() {

					@Override
					public CartItems extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						CartItems cartItems = new CartItems();
						List<Items> items = new ArrayList<>();
						boolean isFirstResultSet = true;
						while (resultSet.next()) {
							if (isFirstResultSet) {
								cartItems.setbranch(resultSet.getString("Branch"));
								cartItems.setType(resultSet.getString("Type"));
								cartItems.setSrl(resultSet.getString("Srl"));
								cartItems.setPrefix(resultSet.getString("Prefix"));
								cartItems.setCaptain(resultSet.getString("Captain"));
								cartItems.setDocdate(resultSet.getString("Docdate"));
								cartItems.setPartyName(resultSet.getString("PartyName"));
								cartItems.setPaxNo(resultSet.getString("px"));
								cartItems.setTableCode(resultSet.getString("Tablecode"));
								cartItems.setDoctime(resultSet.getString("Doctime"));
								cartItems.setTotalbillAmount(getTotal("BillAmount", tableId));
								cartItems.setPartyAddr(resultSet.getString("PartyAddr"));
								cartItems.setPartyContact(resultSet.getString("PartyContact"));
								cartItems.setStore(resultSet.getString("Store"));
								cartItems.setNoofPrints(Integer.valueOf(getTotal("Noofprints", tableId)));
								cartItems.setPartyEmail(resultSet.getString("PartyEmail"));
								cartItems.setRoundoff(getTotal("Roundoff", tableId));
								cartItems.setTotalDiscAmt(getTotal("DiscAmt", tableId));
								cartItems.setHwserial(resultSet.getString("HwSerial"));
								cartItems.setEnteredBy(resultSet.getString("EnteredBy"));
							}
							isFirstResultSet = false;
							items.add(setItemDetauls(resultSet));
						}
						cartItems.setItems(items);
						return cartItems;
					}

				}, tableId);
	}

	private Items setItemDetauls(ResultSet resultSet) throws SQLException {
		Items item = new Items();
		// Items result set
		item.setSno(resultSet.getString("kt_sno"));
		item.setCode(resultSet.getString("kt_Code"));
		item.setQty(resultSet.getInt("kt_Qty"));
		item.setRate(resultSet.getString("kt_Rate"));
		item.setDisc(resultSet.getString("kt_Disc"));
		item.setDiscAmt(resultSet.getString("kt_DiscAmt"));
		item.setTaxCode(resultSet.getString("kt_TaxCode"));
		item.setRemarks(resultSet.getString("kt_Remark"));
		item.setTaxamt(resultSet.getString("kt_Taxamt"));
		item.setAddtaxAmt(resultSet.getString("kt_AddtaxAmt"));
		item.setAddtaxAmt2(resultSet.getString("kt_AddtaxAmt2"));
		item.setItem_name(resultSet.getString("item_name"));
		return item;
	}

	public String getTotal(String coloumnName, String tableId) {
		StringBuilder query = new StringBuilder("select sum(").append(coloumnName).append(")")
				.append("as total from Opentables where Tablecode=?");
		return jdbcTemplate.queryForObject(query.toString(), new String[] { tableId }, String.class);
	}

	/**
	 * 
	 * @param cartItems
	 */
	public void updateRsales(CartItems cartItems) {
		StringBuilder QUERY_FOR_UPDATE_Rstock = new StringBuilder();
		QUERY_FOR_UPDATE_Rstock
				.append("insert into rSales (Branch," + "Canceled," + "Captain," + "Disc," + "DiscAmt," + "DocDate,"
						+ "DocTime," + "NetAmount," + "NoOfPrints," + "PartyAddr," + "PartyContact," + "PartyName,"
						+ "Prefix," + "Px," + "RoundOff," + "Srl," + "Store," + "TableCode," + "TaxAmt," + "TipsAmt,"
						+ "Type," + "TouchValue," + "AuthIDs," + "HWSerial," + "MainType," + "SubType," + "AddTaxAmt,"
						+ "SurchargeAmt," + "PartyEmail" + ") ")
				.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			jdbcTemplate.update(QUERY_FOR_UPDATE_Rstock.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 0;
					ps.setString(++index, cartItems.getBranch());
					ps.setBoolean(++index, false);
					ps.setString(++index, cartItems.getCaptain());
					ps.setString(++index, ""); // Disc
					ps.setString(++index, cartItems.getTotalDiscAmt()); // Disc amt
					ps.setString(++index, cartItems.getDocdate());
					ps.setString(++index, cartItems.getDoctime());
					ps.setString(++index, cartItems.getTotalbillAmount()); // Net Amount
					ps.setInt(++index, cartItems.getNoofPrints());
					ps.setString(++index, cartItems.getPartyAddr());
					ps.setString(++index, cartItems.getPartyContact());
					ps.setString(++index, cartItems.getPartyName());
					ps.setString(++index, cartItems.getPrefix());
					ps.setString(++index, cartItems.getPaxNo()); // PX
					ps.setString(++index, cartItems.getRoundoff()); // RoundOff
					ps.setString(++index, cartItems.getSrl());
					ps.setString(++index, cartItems.getStore());
					ps.setString(++index, cartItems.getTableCode());
					ps.setString(++index, cartItems.getTaxAmt()); // TaxAmt
					ps.setString(++index, ""); // Keep empty Tips
					ps.setString(++index, cartItems.getType());
					ps.setString(++index, cartItems.getTouchValue()); // Touch Value
					ps.setString(++index, cartItems.getCaptain()); // Auth Id
					ps.setString(++index, cartItems.getHwserial());
					ps.setString(++index, "SL"); // MainType
					ps.setString(++index, "RS"); // Sub Type
					ps.setString(++index, cartItems.getAddtaxamt()); // AddTax
					ps.setString(++index, ""); // Keep empty surcharge
					ps.setString(++index, cartItems.getPartyEmail());
				}

			});

		} catch (Exception exception) {
			log.error("Error", exception);
		}
	}

	public void updateRstock(CartItems cartItems) {
		StringBuilder QUERY_FOR_INSERT_RSTOCK = new StringBuilder();
		QUERY_FOR_INSERT_RSTOCK.append(
				"insert into rStock (Branch,Docdate,BillNo,Prefix,Amt,Qty,Code,Canceled,Doctime,Disc,notinstock,paxno,Rate,Remark,Srl,type,TableCode,subtype,taxcode,Touchvalue,Authorise,Authids,Hwserial,Maintype,Enteredby"
						+ ",Captain" + ",DiscAmt" + ",taxamt" + ",Sno" + ",Addtaxamt" + ",Surcharge" + ",taxable)")
				.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		jdbcTemplate.batchUpdate(QUERY_FOR_INSERT_RSTOCK.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
				Items items = cartItems.getItems().get(index);
				int cIndex = 0;
				preparedStatement.setString(++cIndex, cartItems.getBranch());
				preparedStatement.setString(++cIndex, cartItems.getDocdate());
				preparedStatement.setString(++cIndex, cartItems.getBillno());
				preparedStatement.setString(++cIndex, cartItems.getPrefix());
				double amt = Double.valueOf(items.getTaxamt());
				int iAmt = (int) amt;
				iAmt = (iAmt * -1);
				preparedStatement.setString(++cIndex, String.valueOf(iAmt));
				int qnty = items.getQty();
				qnty = qnty * -1;
				preparedStatement.setInt(++cIndex, qnty);
				preparedStatement.setString(++cIndex, items.getCode());
				preparedStatement.setBoolean(++cIndex, false);
				preparedStatement.setString(++cIndex, cartItems.getDoctime());
				preparedStatement.setString(++cIndex, items.getDisc());
				preparedStatement.setInt(++cIndex, 0); // notinStock
				preparedStatement.setString(++cIndex, cartItems.getPaxNo()); // paxNumber
				preparedStatement.setString(++cIndex, "123");
				preparedStatement.setString(++cIndex, items.getRemarks());
				preparedStatement.setString(++cIndex, cartItems.getSrl());
				preparedStatement.setString(++cIndex, cartItems.getType());

				preparedStatement.setString(++cIndex, cartItems.getTableCode());
				preparedStatement.setString(++cIndex, "rs"); // Set SubType
				preparedStatement.setString(++cIndex, items.getTaxCode());
				preparedStatement.setString(++cIndex, cartItems.getTouchValue()); // cartItems.getTouchValue()
				preparedStatement.setString(++cIndex, "A00"); // Set Authorise
				preparedStatement.setString(++cIndex, cartItems.getCaptain()); // SetAuthId

				preparedStatement.setString(++cIndex, cartItems.getHwserial());
				preparedStatement.setString(++cIndex, "SL"); // Set Main Type
				preparedStatement.setString(++cIndex, cartItems.getEnteredBy());
				preparedStatement.setString(++cIndex, cartItems.getCaptain());
				preparedStatement.setString(++cIndex, items.getDiscAmt());
				preparedStatement.setString(++cIndex, items.getTaxamt());

				preparedStatement.setString(++cIndex, items.getSno());
				preparedStatement.setString(++cIndex, items.getAddtaxAmt());
				preparedStatement.setString(++cIndex, ""); // Set Surcharge
				preparedStatement.setString(++cIndex, "");// Taxable

			}

			@Override
			public int getBatchSize() {
				return cartItems.getItems().size();
			}
		});
	}

	public boolean clearKotTableForTable(String tableCode) {
		String clear_kot_for_table = "delete from Kot where TableCode=?";
		String clear_opentable_for_table = "delete from Opentables where tableCode=?";
		return (jdbcTemplate.update(clear_kot_for_table, tableCode) == 1
				&& jdbcTemplate.update(clear_opentable_for_table, tableCode) == 1) ? true : false;
	}

	public String getSRL(String branch) {
		String sql = "Select isnull(Max(srl),0) From rSales Where Branch=? and type ='SAL'";
		return jdbcTemplate.queryForObject(sql, new String[] { branch }, String.class);
	}

	public void updatePaymentDetails(PaymentDetails paymentDetails) {
		String sql = "update rSales set CashAmt=?,CCBank=?,CardAmt=?,Cardnumber=?,ChqAmt=?,Chqno=?,ChqBank=?,DebitAmt=?,Debtors=? ,Status=? ,Settleby=?,Settletime=? where Srl=?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 0;

				ps.setString(++index, paymentDetails.getCashAmt());
				ps.setString(++index, paymentDetails.getcCBank());
				ps.setString(++index, paymentDetails.getCardAmt());
				ps.setString(++index, paymentDetails.getCardnumber());
				ps.setString(++index, paymentDetails.getChqAmt());
				ps.setString(++index, paymentDetails.getChqno());
				ps.setString(++index, paymentDetails.getChqBank());
				ps.setString(++index, paymentDetails.getDebitAmt());
				ps.setString(++index, paymentDetails.getDebtors());
				ps.setString(++index, paymentDetails.getStatus());
				ps.setString(++index, paymentDetails.getSettleby());
				ps.setString(++index, paymentDetails.getSettletime());
				ps.setString(++index, paymentDetails.getSrl());
			}

		});

	}
}
