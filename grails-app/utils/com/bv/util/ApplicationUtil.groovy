package com.bv.util

class ApplicationUtil {

    /**
     * this function take string date like "23-01-2015"("dd-MM-yyyy") as parameter
     * and return "150123"(yyMMdd)
     */
    def spliteDate(def value){
        def temp = value.split("-")
        def tempDate= temp[0]
        def tempMonth= temp[1]
        def tempYear= temp[2]
        def mainStr=tempYear.substring(2, 4)+tempMonth+tempDate
        return mainStr
    }
}
