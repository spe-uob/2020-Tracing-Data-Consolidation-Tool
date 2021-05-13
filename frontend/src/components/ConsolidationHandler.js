import React from 'react';
import styles from './ConsolidationHandler.module.css';
import UploadFile from './UploadFile';

class ConsolidationHandler extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			jobId: null,
			fileProcessed: false,
		}; // TODO display status at the top level
	}

	markFileUploaded(jobId) {
		this.setState({
			jobId,
			fileProcessed: false,
		});
	}

	render() {
		return (
			<div className={styles.groupContainer}>
				<div className={styles.group}>
					<UploadFile markUploaded={this.markFileUploaded.bind(this)}/>
				</div>
			</div>
		);
	}
}

export default ConsolidationHandler;
