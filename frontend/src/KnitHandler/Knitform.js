import React from 'react';
import styles from './Knitform.module.css';
import axios from 'axios';
import ProgressBar from '../ProgressBar/ProgressBar';
import { backendBaseUrl } from '../config';

class Knitform extends React.Component{
	constructor(props){
		super(props);
		this.state = {
			showbar: false,
			status: '',
		};
	}

	sendKnitRequest = () => {
		let data = new FormData();
		data.append('action', 'Knit');

		axios.post(`${backendBaseUrl}/knit`, data).then(res => {
			console.log(res.data); // DEBUG
			this.setState({showbar: true});
		}).catch(err => {
			console.error(err); // DEBUG
			this.setState({status: "Failed to initiate consolidation"});
		});
	}

	onProgressComplete = () => {
		this.setState({status: "Successfully consolidated", showbar: false});
		// TODO onFailure as well?
	}

	render() {
		const {showbar} = this.state;
		return (
			<div className={styles.main}>
				<h1 className={styles.header}>Process Uploaded Files</h1>
				<div className={styles.note}>Please note that processing may take up to 30 seconds.</div>
				<div className={styles.buttonContainer}>
					<button className={styles.button} onClick={() => {
						this.sendKnitRequest();
						console.log(showbar); // DEBUG
					}}>Consolidate</button>
					<a className={styles.button} href={backendBaseUrl + "/Processed.xlsx"}>Download</a>
				</div>
				{showbar ? <ProgressBar onComplete={this.onProgressComplete}></ProgressBar> : null}
				<h4 className={styles.statusMessage}>{this.state.status}</h4>
			</div>
		);
	}
}

export default Knitform;
