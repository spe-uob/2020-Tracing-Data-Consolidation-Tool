import React from 'react';
import styles from './UploadFile.module.css';
import { backendBaseUrl } from '../config';

class UploadFile extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			file: '', 
			status: '',
			id: 0
		};
	}

	onFileChange = (event) => {
		this.setState({
			file: event.target.files[0]
		});
	}

	uploadFileData = (event) => {
		event.preventDefault();
		this.setState({status: ''});

		let data = new FormData();
		data.append('file', this.state.file);

		fetch(`${backendBaseUrl}/upload`, {
			method: 'POST',
			body: data
		}).then((response) => response.json()
		).then((jsonData)=> {
			this.setState({
				status:"file successfully uploaded",
				id: jsonData.jobid
			})
			console.log(jsonData.jobid)
		}).catch(err => {
			this.setState({status: "File failed to upload"});
		});

	}

	render() {
		return (
			<div className={styles.main}>
				<h1 className={styles.header}>Excel Files to Upload</h1>
				<h3 className={styles.header}>Upload a File</h3>
				<input onChange={this.onFileChange} type="file" />
				<button className={styles.button} disabled={!this.state.file} onClick={this.uploadFileData}>Upload</button>
				<h4 className={styles.statusMessage}>{this.state.status}</h4>
			</div>
		)
	}
}

export default UploadFile;
