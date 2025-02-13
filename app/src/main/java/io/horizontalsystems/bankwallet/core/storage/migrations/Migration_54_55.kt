package io.horizontalsystems.bankwallet.core.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_54_55 : Migration(54, 55) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val derivations = listOf("bip44", "bip49", "bip84", "bip86")
        val blockchainTypes = listOf("bitcoin", "litecoin")

        blockchainTypes.forEach { blockchainTypeId ->
            derivations.forEach { derivation ->
                val tokenQueryId = "$blockchainTypeId|native"
                val coinSettingsId = "derivation:$derivation"
                val newTokenQueryId = "$blockchainTypeId|derived:${derivation.replaceFirstChar(Char::titlecase)}"

                db.execSQL(
                    """
                    UPDATE EnabledWallet 
                    SET tokenQueryId = '$newTokenQueryId' 
                    WHERE tokenQueryId = '$tokenQueryId' 
                    AND coinSettingsId = '$coinSettingsId'
                    """
                )
            }
        }

        val bchTypes = listOf("type0", "type145")
        bchTypes.forEach { bchType ->
            val tokenQueryId = "bitcoin-cash|native"
            val coinSettingsId = "bitcoinCashCoinType:$bchType"
            val newTokenQueryId = "bitcoin-cash|address_type:${bchType.replaceFirstChar(Char::titlecase)}"

            db.execSQL(
                """
                UPDATE EnabledWallet 
                SET tokenQueryId = '$newTokenQueryId' 
                WHERE tokenQueryId = '$tokenQueryId' 
                AND coinSettingsId = '$coinSettingsId'
                """
            )
        }

        // Tạo bảng tạm để giữ dữ liệu
        db.execSQL("ALTER TABLE EnabledWallet RENAME TO TempEnabledWallet")

        // Tạo bảng mới không có coinSettingsId
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS EnabledWallet (
                tokenQueryId TEXT NOT NULL, 
                accountId TEXT NOT NULL, 
                walletOrder INTEGER, 
                coinName TEXT, 
                coinCode TEXT, 
                coinDecimals INTEGER, 
                PRIMARY KEY(tokenQueryId, accountId), 
                FOREIGN KEY(accountId) REFERENCES AccountRecord(id) 
                ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
            )
            """
        )

        // Chuyển dữ liệu từ bảng tạm vào bảng mới, loại bỏ coinSettingsId
        db.execSQL(
            """
            INSERT INTO EnabledWallet (tokenQueryId, accountId, walletOrder, coinName, coinCode, coinDecimals)
            SELECT tokenQueryId, accountId, 
                   MAX(walletOrder), MAX(coinName), MAX(coinCode), MAX(coinDecimals)
            FROM TempEnabledWallet
            GROUP BY tokenQueryId, accountId
            """
        )

        // Xóa bảng tạm
        db.execSQL("DROP TABLE IF EXISTS TempEnabledWallet")

        // Thêm index cho accountId
        db.execSQL("CREATE INDEX IF NOT EXISTS index_EnabledWallet_accountId ON EnabledWallet (accountId)")

        // Xóa bảng EnabledWalletCache và tạo lại
        db.execSQL("DROP TABLE IF EXISTS EnabledWalletCache")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS EnabledWalletCache (
                tokenQueryId TEXT NOT NULL, 
                accountId TEXT NOT NULL, 
                balance TEXT NOT NULL, 
                balanceLocked TEXT NOT NULL, 
                PRIMARY KEY(tokenQueryId, accountId), 
                FOREIGN KEY(accountId) REFERENCES AccountRecord(id) 
                ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
            )
            """
        )
    }
}
