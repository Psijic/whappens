package com.psvoid.whappens.data

enum class Country {
    ALA, ALB, AND, ARM, AUT, BLR, BEL, BIH, BGR, HRV, CZE, DNK,
    EST, FRO, FIN, FRA, DEU, GIB, GRC, GGY, VAT, HUN, ISL, IRL,
    IMN, ITA, JEY, LVA, LIE, LTU, LUX, MKD, MLT, MDA, MCO, MNE,
    NLD, NOR, POL, PRT, ROU, RUS, SMR, SRB, SVK, SVN, ESP, SJM,
    SWE, CHE, UKR, GBR
}

enum class LoadingStatus { LOADING, ERROR, DONE }

enum class Month { Jan, Feb, MAr, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec }


sealed class Expr
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

fun a() {
    val b = Const(2.0)
    Sum(b, b)
}