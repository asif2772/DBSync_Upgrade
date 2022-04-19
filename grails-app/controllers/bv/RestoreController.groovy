package bv

class RestoreController {

    DatabaseSynchService databaseSynchService

    def index() {

    }

    def restoreDb(){

    }
    def dumpDB(){

    }
    def excuteFile(){
        def finalResult = ''
        def username =params.username
        def password =params.password
        def webRootDir = servletContext.getRealPath("/")
        def uploadedFile = request.getFile('payload')
        if (!uploadedFile.empty) {
            def userDir = new File(webRootDir, "/mt940/")
            userDir.mkdirs()
            uploadedFile.transferTo(new File(userDir, uploadedFile.originalFilename))

            String fileName = webRootDir + "/mt940/" + uploadedFile.originalFilename;
            def file = new File(fileName)

            def fileNameTemp = uploadedFile.originalFilename
            String fileTempNameTemp = fileNameTemp.replace(".", "bv");

            def fileNameArr = fileTempNameTemp.split("bv");
            Integer indexSize = fileNameArr.size()
            String strExtension = fileNameArr[indexSize - 1];

            if(strExtension == 'sql'){
                finalResult = databaseSynchService.excuteUploadedFile(file,username, password)
            }
        }
        else{

        }

        render(view: "restoreOutput", model: [finalResult: finalResult])


    }
}
