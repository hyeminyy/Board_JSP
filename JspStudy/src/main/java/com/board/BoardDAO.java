package com.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class BoardDAO {
	//의존성 주입(객체를 생성함에 동시에 초기화)
	private Connection conn;
	
	public BoardDAO(Connection conn) {
		this.conn = conn;
	}
	
	public void connectToDatabase() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/JspTest","root","dlsvlslxM12!");
			System.out.println("연결 성공!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//num의 최대값 구하기
	public int getMaxNum() {
		int maxNum = 0;
		
		PreparedStatement psttmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select nvl(max(num),0) from board";
			psttmt = conn.prepareStatement(sql);
			rs = psttmt.executeQuery();
			
			if(rs.next()) {
				maxNum = rs.getInt(1); //컬럼명 or 숫자 씀
				//nvl(max(num))는 파생 컬럼이라 이름을 못써서 1 사용
			}
			rs.close();
			psttmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return maxNum;
	}
	
	//입력(insert) - 넘어오는 데이터는 BoardDTO의 dto
	public int insertData(BoardDTO dto) { 
		int result = 0;
		
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "insert into board (num,name,pwd,email,subject,";
			sql += "content, inAddr, hitCount, created";
			sql += "values (?,?,?,?,?,?,?,0,sysdate)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1,  dto.getNum());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getPwd());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getSubject());
			pstmt.setString(6, dto.getContent());
			pstmt.setString(7, dto.getIpAddr());
			
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	//전체 데이터 갯수 구하기
	public int getDataCount(String searchKey, String searchValue) {
		
		int totalCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			searchValue = "%" + searchValue + "%";
			
			sql = "select nvl(count(*),0) from board ";
			sql += "where " + searchKey + " like ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, searchValue);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				totalCount = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return totalCount;
	}
	//전체 데이터 출력 페이지마다 개수 제한)
	public List<BoardDTO> getLists(int start, int end, String searchKey, String searchValue){
		//rownum을 매개변수로 할당해서 해당 범위만 list로 출력
	List<BoardDTO> lists = new ArrayList<BoardDTO>();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	
	try {
		searchValue = "%" + searchValue + "%";
		
		sql = "select * from (";
		sql += "select rownum rnum, data.* from (";
		sql += "select num, name, subject, hitcount,";
		sql += "to_char(created, 'YYYY-MM-DD') created ";
		sql += "from board where " + searchKey + " like ? ";
		sql += "order by num desc) data)";
		sql += "where rnunm >= ? and rnum <= ?";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, searchValue);
		pstmt.setInt(2, start);
		pstmt.setInt(3, end);
		
		rs = pstmt.executeQuery();
		
		while(rs.next()) {
			
		BoardDTO dto = new BoardDTO();
		
		dto.setNum(rs.getInt("num"));
		dto.setName(rs.getString("name"));
		dto.setSubject(rs.getString("subject"));
		dto.setHitCount(rs.getInt("hitCount"));
		dto.setCreated(rs.getString("created"));
		
		lists.add(dto);
		
		}
		rs.close();
		pstmt.close();
		
	} catch (Exception e) {
		System.out.println(e.toString());
		}
		return lists;
	}
	
	//num으로 조회한 한개의 데이터
	public BoardDTO getReadData(int num) {
		BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select num,name, pwd,email,subject,content,";
			sql += "ipAddr, hitCount, created ";
			sql += "from board where num=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new BoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setName(rs.getString("name"));
				dto.setPwd(rs.getString("pwd"));
				dto.setEmail(rs.getString("email"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setIpAddr(rs.getString("ipAddr"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
	//조회수 증가
	public int updateHitCount(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "update board set hitCount = hitCount + 1 where num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	//수정
	public int updateData(BoardDTO dto) {
		int result = 0;
		
		PreparedStatement pstmt = null;
		//sql 주입 방식
		//Java에서 SQL 쿼리를 실행할 때 사용하는 객체로, 주로 SQL 주입 방지와
		//성능 최적화를 위해 사용한다.
		String sql;
		
		try {
			sql = "update board set name=?, pwwd=?, email=?, subject=?, ";
			sql += "content=? where num=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getPwd());
			pstmt.setString(3, dto.getEmail());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			pstmt.setInt(6, dto.getNum());
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	public int deleteData(int num) {
		int result = 0;
		
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "delete board where num=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
}



















