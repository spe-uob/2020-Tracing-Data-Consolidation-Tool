import React from 'react';
import styles from './ConsolidationHandler.module.css';
import UploadFile from './UploadFile';
import DownloadFile from './DownloadFile';

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
			<div className={this.props.className}>
				<div className={styles.group}>
					<UploadFile markUploaded={this.markFileUploaded.bind(this)}/>
				</div>
				<div className={styles.group}>
					<DownloadFile jobId={this.state.jobId} fileProcessed={this.state.fileProcessed}
						markFileProcessed={() => this.setState({ fileProcessed: true })} />
				</div>
			</div>
		);
	}
}

export default ConsolidationHandler;
