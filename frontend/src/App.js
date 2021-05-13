import styles from './App.module.css';
import ConsolidationHandler from './components/ConsolidationHandler';

function App() {
	return (
		<div className={styles.main}>
			<div className={styles.headerContainer}>
				<h1 className={styles.header}>Tracing Data Consolidation Tool</h1>
			</div>
			<div className={styles.content}>
				<ConsolidationHandler />
			</div>
		</div>
	);
}

export default App;
