package com.test.fileCopy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;

//ftp.host =ftp.bmg.com
//
//ftp.user=bmg_cwr
//
//ftp.pass=N905S1I0Br3D

/**

 * This is a FTP service to copy files from partner location to monetize

 * location
 * 
 * @author Sandip

 */
@Component
public class SftpFileCopyService {

	public SftpFileCopyService() {}

	private static final Logger LOGGER = LoggerFactory.getLogger(SftpFileCopyService.class);


	/*@Value("${gdrive.acknowledgementFileLocation}")
	private String acknowlegementFileLocation;

	@Value("${sftp.port}")
	private int port;*/

	@PersistenceContext(name = "SQLPersistence")
	private EntityManager entityManager;

	/**
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Scheduled(fixedDelay = 100 * 1000 * 60)
	public void copyFTPFiles() throws Exception {
		
		System.out.println("1 aug 2017");
	}

  
	/**

	 * 

	 * @param fileNames

	 */

	public void saveAckFileNamesInDB(String name, String fileDate) {


		try{

			Date insertDbDate = null;

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			insertDbDate = simpleDateFormat.parse(fileDate);




			entityManager.createNativeQuery("insert into mt_AcknowledgementFTP(ackFileName, lastModifiedDate)"

					+ " values (?, ?) ")

			.setParameter(1, name)   

			.setParameter(2, insertDbDate)

			.executeUpdate();

		}

		catch(Exception e){

			e.printStackTrace();

		}

	}




	/**

	 * 

	 * @param fileNames

	 * @return

	 * @throws Exception

	 */

	public Boolean isAckFileAlreadyCopied(List<String> fileNames) throws Exception {




		boolean flag = false;

		String name = null;

		String fileDate =  null; 

		Date insertDbDate = null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");




		for (int i = 0; i < fileNames.size(); i++) {




			name = fileNames.get(0);

			fileDate = fileNames.get(1);




			try {

				insertDbDate = simpleDateFormat.parse(fileDate);

			} 

			catch (ParseException e1) {

				e1.printStackTrace();

			}

			LOGGER.info("Name -> "+name +" fileDate -> "+fileDate +  "  insertDbDate -> "+ insertDbDate);




			try{

				entityManager.createNativeQuery("select max ( "+ insertDbDate +" ) from mt_AcknowledgementFTP)")

				.executeUpdate();

			}

			catch(Exception e){

				e.printStackTrace();

			}

		}

		return flag;	

	}




	public Boolean isAckFileNameUnique(List<String> ackFile) throws Exception {




		boolean flag = false;

		// checking whether ACK name is unique or already exists.

		for(String aFileName : ackFile){

			int status = entityManager.createNativeQuery("select ackFileName from mt_AcknowledgementFTP where ackFileName = ( "+ aFileName +" ) ")

					.executeUpdate(); 




			if (status !=0) {

				flag = true;

			}

		}

		return flag;	

	}

	/**

	 * 

	 * @return

	 * @throws Exception

	 */

	@SuppressWarnings("unchecked")

	public boolean checkCopiedFileNameEntryInDB() throws Exception {




		boolean flag = false;

		List<String> copiedFileNameList = new ArrayList<>();




		Query fileQuery = entityManager.createNativeQuery("select ackFileName from mt_AcknowledgementFTP"); 

		copiedFileNameList = fileQuery.getResultList();




		String fileNameList = 	copiedFileNameList.toString().replaceAll("(^\\[|\\]$)", "");




		for(String st : copiedFileNameList){




			if(st.contains(fileNameList)){ 

				flag=true;

			}

			else{

				flag=false;

			}

		}

		return flag; 

	}

}


