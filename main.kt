/***********************************************************************************************
 * Name: Patrick James T. Marcellana
 * Language: Kotlin
 * Paradigm: Multi-paradigm
 * This program implements a Tax Calculator that uses the revised Philippine Tax Table for 2022.
 **********************************************************************************************/

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Runs the program.
 */
fun main() {
    /* Get Monthly Income from User */
    println("-".repeat(130)) // divider for visual aid purposes
    println("TAX CALCULATOR PHILIPPINES 2022\n")
    print("Enter monthly income: Php ")
    val monthlyIncome = readLine()!!.toBigDecimal()

    /* Computation of Contributions */
    val sssCon = computeSSS(monthlyIncome)
    val philHealthCon = computePhilHealth(monthlyIncome)
    val pagIBIGCon = computePagIBIG(monthlyIncome)
    val totalCon = (sssCon + philHealthCon + pagIBIGCon).setScale(2, RoundingMode.HALF_UP)
    
    /* Tax Computation */
    val taxableIncome = monthlyIncome - totalCon
    val incomeTax = computeIncomeTax(taxableIncome)
    val netPayAfterTax = (monthlyIncome - incomeTax).setScale(2, RoundingMode.HALF_UP)
    val totalDeductions = (incomeTax + totalCon).setScale(2, RoundingMode.HALF_UP)
    val finalMonthlyNetPay = (monthlyIncome - totalDeductions).setScale(2, RoundingMode.HALF_UP)

    /* Display Results */
    println("-".repeat(130)) // divider for visual aid purposes
    println("\nCOMPUTATION RESULTS")
    println("\nSSS Contribution: Php " + sssCon)
    println("PhilHealth Contribution: Php " + philHealthCon)
    println("Pag-IBIG Contribution: Php " + pagIBIGCon)
    println("Total Contributions: Php " + totalCon)
    println("\nIncome Tax: Php " + incomeTax + if(incomeTax == BigDecimal("0.00")) {" - Tax Exempted"} else {""})
    println("Net Pay After Tax: Php " + netPayAfterTax)
    println("\nTotal Deductions: Php " + totalDeductions)
    println("Net Pay After Deductions: Php " + finalMonthlyNetPay)
}

/**
 * Calculates the SSS contribution given the monthly income.
 * @param monthlyIncome the user's monthly income
 * @return SSS contribution
*/
fun computeSSS(monthlyIncome: BigDecimal): BigDecimal {
    // fill up SSS table with columns: lower bound, upper bound, employee's total sss contribution
    val rangeCnt = 45
    val sssTable = ArrayList<ArrayList<BigDecimal>>(rangeCnt) 
    sssTable += arrayListOf(BigDecimal("0.00"), BigDecimal("3249.99"), BigDecimal("135.00"))
    for(index in 1 until rangeCnt) {
        sssTable += arrayListOf(sssTable[index - 1][1] + BigDecimal("0.01"), 
                                sssTable[index - 1][1] + BigDecimal("500.00"),
                                sssTable[index - 1][2] + BigDecimal("22.50"))
    }

    // determine user's total sss contribution based on monthly income and the sss table
    var totalSSSContribution = BigDecimal("0.00")
    for(index in 0 until rangeCnt)
        if((index < rangeCnt - 1 && monthlyIncome >= sssTable[index][0] && monthlyIncome <= sssTable[index][1]) ||
           (index == rangeCnt - 1 && monthlyIncome >= sssTable[index][0])) {
                totalSSSContribution = sssTable[index][2]
                break
        }

    // return employee's SSS contribution by mgetting 4.5% of the monthly salary credit
    return totalSSSContribution.setScale(2, RoundingMode.HALF_UP)
}

/**
 * Calculates the PhilHealth contribution given the monthly income.
 * @param monthlyIncome the user's monthly income
 * @return PhilHealth contribution
*/
fun computePhilHealth(monthlyIncome: BigDecimal): BigDecimal {
    // compute for monthly basic salary based on the table range
    val monthlyBasicSalary = when {
        monthlyIncome <= BigDecimal("10000.00") -> BigDecimal("10000.00")
        monthlyIncome >= BigDecimal("80000.00") -> BigDecimal("80000.00")
        else -> monthlyIncome
    }

    // philhealth contribution = salary * premium rate / 2 (50% employee, 50% employer)
    val premiumRate = BigDecimal("0.04") // 2022 premium rate
    return (monthlyBasicSalary * premiumRate / BigDecimal("2.00")).setScale(2, RoundingMode.HALF_UP)
}

/**
 * Calculates the Pag-IBIG contribution given the monthly income.
 * @param monthlyIncome the user's monthly income
 * @return Pag-IBIG contribution
*/
fun computePagIBIG(monthlyIncome: BigDecimal): BigDecimal {
    // determine monthly compensation
    val monthlyCompensation = when {
        monthlyIncome < BigDecimal("5000.00") -> monthlyIncome
        else -> BigDecimal("5000.00")
    }

    // determine rate based on monthly compensation
    val rate = when {
        monthlyCompensation <= BigDecimal("1500.00") -> BigDecimal("0.01")
        else -> BigDecimal("0.02")
    }

    // compute Pag-IBIG contribution
    return (monthlyCompensation * rate).setScale(2, RoundingMode.HALF_UP)
}

/**
 * Calculates the Income Tax given the monthly income.
 * @param monthlyIncome the user's monthly income
 * @return user's income tax
*/
fun computeIncomeTax(taxableIncome: BigDecimal): BigDecimal {
    // fill up monthly withholding tax table with columns: lower bound, upper bound, fixed tax, tax rate on excess
    val bracketCnt = 6
    val taxTable = ArrayList<ArrayList<BigDecimal>>(bracketCnt) 
    taxTable += arrayListOf(BigDecimal("0.00"), BigDecimal("20833.00"), BigDecimal("0.00"), BigDecimal("0.00"))
    taxTable += arrayListOf(BigDecimal("20833.00"), BigDecimal("33332.00"), BigDecimal("0.00"), BigDecimal("0.20"))
    taxTable += arrayListOf(BigDecimal("33333.00"), BigDecimal("66666.00"), BigDecimal("2500.00"), BigDecimal("0.25"))
    taxTable += arrayListOf(BigDecimal("66667.00"), BigDecimal("166666.00"), BigDecimal("10833.33"), BigDecimal("0.30"))
    taxTable += arrayListOf(BigDecimal("166667.00"), BigDecimal("666666.00"), BigDecimal("40833.33"), BigDecimal("0.32"))
    taxTable += arrayListOf(BigDecimal("666667.00"), BigDecimal("-1.00"), BigDecimal("200833.33"), BigDecimal("0.35"))

    // determine fixed tax, rate, and compensation level
    var fixedTax = BigDecimal("0.00"); var rate = BigDecimal("0.00"); var compLevel = BigDecimal("0.00")
    for(index in 0 until bracketCnt) {
        if((index < bracketCnt -  1 && taxableIncome >= taxTable[index][0] && taxableIncome <= taxTable[index][1]) ||
           (index == bracketCnt - 1 && taxableIncome >= taxTable[index][0])) {
                fixedTax = taxTable[index][2]
                rate = taxTable[index][3]
                compLevel = taxTable[index][0]
                break
        }
    }

    // compute income tax
    return ((taxableIncome - compLevel) * rate + fixedTax).setScale(2, RoundingMode.HALF_UP)
}