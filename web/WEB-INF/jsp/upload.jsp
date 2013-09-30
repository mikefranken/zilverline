<%@ include file="header.jsp"%>

        <script type="text/javascript">
        function addFileInput() {
			var myFileCountObj = document.getElementById("fileCount");
	   		myFileCountObj.value++;

			var myFileElement = document.createElement("input");
			myFileElement.setAttribute("type", "file");
			myFileElement.setAttribute("size", "40");
			myFileElement.setAttribute("ONCHANGE", "addFileInput();");
			myFileElement.setAttribute("name", "files[file"+myFileCountObj.value+']');
			
			var myFileContainer = document.getElementById("fileContainer");
			myFileContainer.appendChild(document.createElement("br"));
			myFileContainer.appendChild(myFileElement);
			myFileContainer.appendChild(document.createElement("br"));
			myFileContainer.appendChild(document.createElement("br"));
        }	
        </script>
        
        <form name="uploader" method="POST" enctype="multipart/form-data">
           
            <spring:bind path="command.fileCount">
            <input type="hidden" id="fileCount" name="fileCount" value="<c:out value="${command.fileCount}"/>"/>
            </spring:bind>
  
		<div class="zilverpanel">Upload file(s) to a collection.
            <br/>
            <table><tr><td width="30%">Select a collection
            <spring:bind path="collections">
				<SELECT name="<c:out value="collectionName"/>">
				<c:forEach items="${collections}" var="collection">
					<OPTION value="<c:out value="${collection}"/>">
			            <c:out value="${collection}"/>
				</c:forEach>
				</SELECT>
            </spring:bind>
			</td>
			<td>
            <div id="fileContainer">
	            <c:forEach begin="0" var="current" end="${command.fileCount-1}">
	            <br/>
	                <spring:bind path="command.files[file${current}]" >
	                  <div class="error">
	                  	<input type="file" size="40" 
	                  	<c:if test="${current == command.fileCount-1}">ONCHANGE="addFileInput();" </c:if>
	                  	name="files[file<c:out value="${current}"/>]">
	                  	<c:out value="${status.errorMessage}" />
	                  </div>
	                  <br/>
	                </spring:bind>
	            </c:forEach>
             </div>
             <br><br></td></tr></table>
         </div>
			<p>
            
              <INPUT type="submit" alignment="center" value="Upload"/>
              <INPUT type="submit" name="_cancel" value="<fmt:message key="Cancel"/>" />
        </form>
        <p>
		<c:if test="${!empty files}">
			<span class="doc">Succesfully uploaded the following files to Collection <c:out value="${collection}" />:
			<ul>
			<c:forEach var="file" items="${files}">
				<li>
					<c:out value="${file}" />
				</li>
			</c:forEach>
			</ul></span>
		</c:if>     
		<div>
			<spring:bind path="command.*">
			<c:if test="${!empty status.errorMessages}">
			<p>An Error occured.
				<c:forEach var="error" items="${status.errorMessages}">
					<p>
					<div class="error"><c:out value="${error}" /></div>
				</c:forEach>
			</p>
			</c:if>	
			</spring:bind>
		</p>
		</div>
		
		
		      

<%@ include file="footer.jsp"%>