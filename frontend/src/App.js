import styles from './App.module.css';
import UploadFile from './Uploadhandler/UploadFile';
import Knitform from './KnitHandler/Knitform';

function App() {
	return (
		<div className={styles.main}>
			<div className={styles.headerContainer}>
				<h1 className={styles.header}>Tracing Data Consolidation Tool</h1>
			</div>
			<div className={styles.content}>
				<div className={styles.groupContainer}>
					<div className={styles.group}>
						<UploadFile />
					</div>
					<div className={styles.group}>
						<Knitform />
					</div>
				</div>
			</div>
		</div>
	);
}

export default App;
